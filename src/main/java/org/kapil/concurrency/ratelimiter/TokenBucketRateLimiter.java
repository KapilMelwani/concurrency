package org.kapil.concurrency.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketRateLimiter implements RateLimiter {
    // Maximum number of tokens that can be accumulated in the bucket.
    private final long threshold;
    // Time unit for the token refill (in milliseconds).
    private final long windowUnit = 1000;
    // Current number of available tokens.
    private final AtomicLong tokens = new AtomicLong(0);
    // Timestamp of the last refill operation.
    private volatile long lastRefillTimestamp = System.currentTimeMillis();

    /**
     * Constructs a TokenBucketRateLimiter with the specified threshold.
     *
     * @param threshold the maximum number of tokens that can be accumulated.
     */
    public TokenBucketRateLimiter(long threshold) {
        this.threshold = threshold;
        // Initialize the bucket to full capacity.
        this.tokens.set(threshold);
    }

    /**
     * Attempts to acquire a token from the bucket.
     *
     * @return true if a token was successfully acquired; false otherwise.
     */
    @Override
    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        // Calculate the number of tokens to add based on elapsed time since the last refill.
        long tokensToAdd = ((currentTime - lastRefillTimestamp) / windowUnit) * threshold;

        // Refill the bucket with the calculated number of tokens.
        if (tokensToAdd > 0) {
            // Ensure the bucket does not exceed its capacity.
            tokens.set(Math.min(threshold, tokens.addAndGet(tokensToAdd)));
            // Update the refill timestamp.
            lastRefillTimestamp = currentTime;
        }

        // Attempt to acquire a token.
        if (tokens.get() > 0) {
            // Decrement the token count and grant access.
            tokens.decrementAndGet();
            return true;
        }

        // Token bucket is empty; deny access.
        return false;
    }
}