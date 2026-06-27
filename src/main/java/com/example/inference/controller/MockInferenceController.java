package com.example.inference.controller;

import com.example.inference.ratelimit.FixedWindowRateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
@Tag(name = "Mock Inference", description = "Mock rate-limited inference endpoint")
public class MockInferenceController {

    private final FixedWindowRateLimiter serverRateLimiter;
    private final long simulatedLatencyMs;

    public MockInferenceController(
            @Value("${inference.mock.rate-limit-per-second:10}") int rateLimitPerSecond,
            @Value("${inference.mock.simulated-latency-ms:200}") long simulatedLatencyMs) {
        this.serverRateLimiter = new FixedWindowRateLimiter(rateLimitPerSecond);
        this.simulatedLatencyMs = simulatedLatencyMs;
    }

    @PostMapping("/infer")
    @Operation(summary = "Mock inference endpoint with rate limiting")
    public ResponseEntity<MockInferenceResponse> infer(@RequestBody MockInferenceRequest request) throws InterruptedException {
        if (!serverRateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "1")
                    .build();
        }

        if (simulatedLatencyMs > 0) {
            Thread.sleep(simulatedLatencyMs);
        }

        String prompt = request.getPrompt() != null ? request.getPrompt() : "";
        return ResponseEntity.ok(new MockInferenceResponse("Response for: " + prompt));
    }

    public static class MockInferenceRequest {

        private String prompt;

        public MockInferenceRequest() {
        }

        public MockInferenceRequest(String prompt) {
            this.prompt = prompt;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }

    public static class MockInferenceResponse {

        private String inference;

        public MockInferenceResponse() {
        }

        public MockInferenceResponse(String inference) {
            this.inference = inference;
        }

        public String getInference() {
            return inference;
        }

        public void setInference(String inference) {
            this.inference = inference;
        }
    }
}
