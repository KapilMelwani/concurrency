package org.kapil.concurrency.ratelimiter;

public interface RateLimiter {
    boolean tryAcquire();
}