package com.example.inference.ratelimit;

import java.util.concurrent.TimeUnit;

/**
 * Strict no-burst rate limiter: one permit every 1/rateLimitPerSecond seconds.
 */
public class StrictRateLimiter implements RateLimiter {

    private final long intervalNanos;
    private long nextAvailableNanos;

    public StrictRateLimiter(int rateLimitPerSecond) {
        if (rateLimitPerSecond <= 0) {
            throw new IllegalArgumentException("rateLimitPerSecond must be positive");
        }
        this.intervalNanos = 1_000_000_000L / rateLimitPerSecond;
        this.nextAvailableNanos = System.nanoTime();
    }

    @Override
    public synchronized void acquire() throws InterruptedException {
        waitUntilAvailable();
        scheduleNextPermit();
    }

    @Override
    public synchronized boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        if (System.nanoTime() >= nextAvailableNanos) {
            scheduleNextPermit();
            return true;
        }
        long waitNanos = nextAvailableNanos - System.nanoTime();
        if (waitNanos > deadline - System.nanoTime()) {
            return false;
        }
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis(waitNanos));
        scheduleNextPermit();
        return true;
    }

    private void waitUntilAvailable() throws InterruptedException {
        long now = System.nanoTime();
        if (now < nextAvailableNanos) {
            long waitMs = TimeUnit.NANOSECONDS.toMillis(nextAvailableNanos - now);
            if (waitMs > 0) {
                Thread.sleep(waitMs);
            }
        }
    }

    private void scheduleNextPermit() {
        long now = System.nanoTime();
        nextAvailableNanos = Math.max(now, nextAvailableNanos) + intervalNanos;
    }
}
