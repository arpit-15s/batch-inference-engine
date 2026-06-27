package com.example.inference.ratelimit;

import java.util.concurrent.TimeUnit;

public interface RateLimiter {

    void acquire() throws InterruptedException;

    boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException;
}
