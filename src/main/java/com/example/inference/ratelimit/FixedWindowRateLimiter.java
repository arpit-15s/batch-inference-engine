package com.example.inference.ratelimit;

/**
 * Fixed one-second window counter for server-side rate limiting (no burst).
 */
public class FixedWindowRateLimiter {

    private final int limitPerSecond;
    private long currentWindowSecond = -1;
    private int requestCount;

    public FixedWindowRateLimiter(int limitPerSecond) {
        if (limitPerSecond <= 0) {
            throw new IllegalArgumentException("limitPerSecond must be positive");
        }
        this.limitPerSecond = limitPerSecond;
    }

    public synchronized boolean tryAcquire() {
        long windowSecond = System.currentTimeMillis() / 1000;
        if (windowSecond != currentWindowSecond) {
            currentWindowSecond = windowSecond;
            requestCount = 0;
        }
        if (requestCount >= limitPerSecond) {
            return false;
        }
        requestCount++;
        return true;
    }
}
