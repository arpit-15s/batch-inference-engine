package com.example.inference.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
@Tag(name = "Mock Inference", description = "Mock rate-limited inference endpoint")
public class MockInferenceController {

    @PostMapping("/infer")
    @Operation(summary = "Mock inference endpoint with rate limiting")
    public ResponseEntity<MockInferenceResponse> infer(@RequestBody MockInferenceRequest request) {
        // TODO: implement simulated latency and rate limiting
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static class MockInferenceRequest {

        private String prompt;

        public MockInferenceRequest() {
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
