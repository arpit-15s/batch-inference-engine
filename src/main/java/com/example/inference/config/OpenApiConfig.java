package com.example.inference.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI batchInferenceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Batch Inference Engine API")
                        .description("Processes batches of prompts concurrently against a mock rate-limited inference endpoint")
                        .version("0.0.1"));
    }
}
