package com.example.inference.service;

import com.example.inference.model.PromptResult;
import com.example.inference.ratelimit.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InferenceClientServiceImplTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RateLimiter rateLimiter;

    private List<Long> backoffCalls;
    private InferenceClientServiceImpl inferenceClientService;

    @BeforeEach
    void setUp() {
        backoffCalls = new ArrayList<>();
        inferenceClientService = new InferenceClientServiceImpl(
                restClient,
                rateLimiter,
                3,
                100,
                5000,
                millis -> backoffCalls.add(millis));
    }

    @Test
    void retriesOn429AndEventuallySucceedsWithoutFailingBatch() throws Exception {
        doNothing().when(rateLimiter).acquire();
        AtomicInteger attempts = new AtomicInteger();

        PromptResult result = inferenceClientService.inferWithRetry(
                "hello",
                0,
                prompt -> {
                    if (attempts.getAndIncrement() == 0) {
                        return InferenceClientServiceImpl.InferenceCallResult.retryable(50);
                    }
                    return InferenceClientServiceImpl.InferenceCallResult.success("world");
                });

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getInference()).isEqualTo("world");
        assertThat(attempts.get()).isEqualTo(2);
        assertThat(backoffCalls).isNotEmpty();
        verify(rateLimiter, org.mockito.Mockito.times(2)).acquire();
    }

    @Test
    void exhausted429RetriesMarkPromptFailedButDoNotThrow() throws Exception {
        doNothing().when(rateLimiter).acquire();

        PromptResult result = inferenceClientService.inferWithRetry(
                "hello",
                1,
                prompt -> InferenceClientServiceImpl.InferenceCallResult.retryable(0));

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getIndex()).isEqualTo(1);
        assertThat(result.getPrompt()).isEqualTo("hello");
        assertThat(backoffCalls).hasSize(3);
    }

    @Test
    void nonRetryableClientErrorFailsImmediately() throws Exception {
        doNothing().when(rateLimiter).acquire();

        PromptResult result = inferenceClientService.inferWithRetry(
                "hello",
                0,
                prompt -> InferenceClientServiceImpl.InferenceCallResult.failed());

        assertThat(result.isSuccess()).isFalse();
        assertThat(backoffCalls).isEmpty();
    }
}
