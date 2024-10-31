package org.kapil.concurrency.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;

public class LeakyBucketRateLimiter implements RateLimiter {
    // The maximum capacity of the bucket. Once water reaches this level, further requests are rejected.
    private final long threshold;
    // Time unit for measuring the leak rate, set to one second (1000 milliseconds).
    private final long windowUnit = 1000;
    // Current level of water in the bucket, managed atomically to ensure thread safety.
    private final AtomicLong water = new AtomicLong(0);
    // Timestamp of the last leak calculation, used to determine how much water has leaked over time.
    private volatile long lastLeakTimestamp;

    /**
     * Constructs a LeakyBucketRateLimiter with a specified threshold.
     *
     * @param threshold the maximum number of requests that can be handled in the time window.
     */
    public LeakyBucketRateLimiter(long threshold) {
        this.threshold = threshold;
        this.lastLeakTimestamp = System.currentTimeMillis();
    }

    /**
     * Tries to acquire a permit for a request based on the current state of the bucket.
     *
     * @return true if the request can be accommodated (water level is below threshold), false otherwise.
     */
    @Override
    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        // Calculate the amount of water that has leaked since the last check.
        long leakedAmount = ((currentTime - lastLeakTimestamp) / windowUnit) * threshold;

        // If any water has leaked, reduce the water level accordingly.
        if (leakedAmount > 0) {
            water.addAndGet(-leakedAmount);
            lastLeakTimestamp = currentTime;
        }

        // Ensure the water level does not go below zero.
        if (water.get() < 0) {
            water.set(0);
        }

        // If the bucket is not full, increment the water level and allow the request.
        if (water.get() < threshold) {
            water.getAndIncrement();
            return true;
        }

        // If the bucket is full, reject the request.
        return false;
    }
}