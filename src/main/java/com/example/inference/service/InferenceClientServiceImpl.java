package com.example.inference.service;

import com.example.inference.controller.MockInferenceController;
import com.example.inference.model.PromptResult;
import com.example.inference.ratelimit.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class InferenceClientServiceImpl implements InferenceClientService {

    private final RestClient restClient;
    private final RateLimiter rateLimiter;
    private final int maxRetries;
    private final long backoffInitialMs;
    private final long backoffMaxMs;
    private final Sleeper sleeper;

    @Autowired
    public InferenceClientServiceImpl(RestClient inferenceRestClient,
                                      RateLimiter rateLimiter,
                                      @Value("${inference.max-retries:3}") int maxRetries,
                                      @Value("${inference.backoff-initial-ms:100}") long backoffInitialMs,
                                      @Value("${inference.backoff-max-ms:5000}") long backoffMaxMs) {
        this.restClient = inferenceRestClient;
        this.rateLimiter = rateLimiter;
        this.maxRetries = maxRetries;
        this.backoffInitialMs = backoffInitialMs;
        this.backoffMaxMs = backoffMaxMs;
        this.sleeper = Thread::sleep;
    }

    InferenceClientServiceImpl(RestClient inferenceRestClient,
                               RateLimiter rateLimiter,
                               int maxRetries,
                               long backoffInitialMs,
                               long backoffMaxMs,
                               Sleeper sleeper) {
        this.restClient = inferenceRestClient;
        this.rateLimiter = rateLimiter;
        this.maxRetries = maxRetries;
        this.backoffInitialMs = backoffInitialMs;
        this.backoffMaxMs = backoffMaxMs;
        this.sleeper = sleeper;
    }

    @Override
    public PromptResult infer(String prompt, int index) {
        return inferWithRetry(prompt, index, this::callMockApi);
    }

    PromptResult inferWithRetry(String prompt,
                                int index,
                                InferenceCall inferenceCall) {
        int attempt = 0;
        while (true) {
            try {
                rateLimiter.acquire();
                long startMs = System.currentTimeMillis();
                InferenceCallResult callResult = inferenceCall.execute(prompt);
                long latencyMs = System.currentTimeMillis() - startMs;

                if (callResult.success()) {
                    PromptResult result = new PromptResult();
                    result.setIndex(index);
                    result.setPrompt(prompt);
                    result.setInference(callResult.inference());
                    result.setLatencyMs(latencyMs);
                    result.setSuccess(true);
                    return result;
                }

                if (!callResult.retryable() || attempt >= maxRetries) {
                    return failedResult(prompt, index, latencyMs);
                }

                attempt++;
                backoff(attempt, callResult.retryAfterMs());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return failedResult(prompt, index, 0);
            }
        }
    }

    private InferenceCallResult callMockApi(String prompt) {
        try {
            MockInferenceController.MockInferenceResponse body = restClient.post()
                    .uri("/infer")
                    .body(new MockInferenceController.MockInferenceRequest(prompt))
                    .retrieve()
                    .body(MockInferenceController.MockInferenceResponse.class);
            return InferenceCallResult.success(body != null ? body.getInference() : "");
        } catch (RestClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();
            if (status.value() == 429) {
                long retryAfterMs = parseRetryAfter(ex.getResponseHeaders().getFirst("Retry-After"));
                return InferenceCallResult.retryable(retryAfterMs);
            }
            if (status.is5xxServerError()) {
                return InferenceCallResult.retryable(0);
            }
            return InferenceCallResult.failed();
        }
    }

    private void backoff(int attempt, long retryAfterMs) throws InterruptedException {
        if (retryAfterMs > 0) {
            sleeper.sleep(retryAfterMs);
            return;
        }
        long exponential = backoffInitialMs * (1L << (attempt - 1));
        long capped = Math.min(exponential, backoffMaxMs);
        long jitter = ThreadLocalRandom.current().nextLong(0, Math.max(1, capped / 2));
        sleeper.sleep(capped + jitter);
    }

    private long parseRetryAfter(String retryAfterHeader) {
        if (retryAfterHeader == null || retryAfterHeader.isBlank()) {
            return 0;
        }
        try {
            return Long.parseLong(retryAfterHeader.trim()) * 1000;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private PromptResult failedResult(String prompt, int index, long latencyMs) {
        PromptResult result = new PromptResult();
        result.setIndex(index);
        result.setPrompt(prompt);
        result.setLatencyMs(latencyMs);
        result.setSuccess(false);
        return result;
    }

    @FunctionalInterface
    interface InferenceCall {
        InferenceCallResult execute(String prompt);
    }

    record InferenceCallResult(boolean success, boolean retryable, String inference, long retryAfterMs) {

        static InferenceCallResult success(String inference) {
            return new InferenceCallResult(true, false, inference, 0);
        }

        static InferenceCallResult retryable(long retryAfterMs) {
            return new InferenceCallResult(false, true, null, retryAfterMs);
        }

        static InferenceCallResult failed() {
            return new InferenceCallResult(false, false, null, 0);
        }
    }

    @FunctionalInterface
    interface Sleeper {
        void sleep(long millis) throws InterruptedException;
    }
}
