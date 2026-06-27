package com.example.inference.config;

import com.example.inference.ratelimit.RateLimiter;
import com.example.inference.ratelimit.StrictRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Value("${inference.mock.rate-limit-per-second:10}")
    private int rateLimitPerSecond;

    @Bean
    public RateLimiter rateLimiter() {
        return new StrictRateLimiter(rateLimitPerSecond);
    }
}
