package com.example.inference.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimiterConfig {

    @Value("${inference.mock.rate-limit-per-second:10}")
    private int rateLimitPerSecond;

    @Bean
    public RateLimiter rateLimiter() {
        return new RateLimiter(rateLimitPerSecond);
    }

    /**
     * Simple no-burst rate limiter placeholder.
     * Business logic for token acquisition will be implemented later.
     */
    public static class RateLimiter {

        private final int permitsPerSecond;
        private final Semaphore semaphore;

        public RateLimiter(int permitsPerSecond) {
            this.permitsPerSecond = permitsPerSecond;
            this.semaphore = new Semaphore(permitsPerSecond, true);
        }

        public void acquire() throws InterruptedException {
            // TODO: implement strict no-burst token scheduling
            semaphore.acquire();
        }

        public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
            // TODO: implement strict no-burst token scheduling
            return semaphore.tryAcquire(timeout, unit);
        }

        public int getPermitsPerSecond() {
            return permitsPerSecond;
        }
    }
}
