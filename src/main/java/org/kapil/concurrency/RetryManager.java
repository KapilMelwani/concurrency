package org.kapil.concurrency;

public class RetryManager {
  private final int maxAttempts;
  private final long initialDelay;

  public RetryManager(int maxAttempts, long initialDelay) {
    this.maxAttempts = maxAttempts;
    this.initialDelay = initialDelay;
  }

  public void executeWithRetry(Runnable operation) {
    int attempts = 0;
    long delay = initialDelay;

    while (attempts < maxAttempts) {
      try {
        operation.run();
        return;
      } catch (Exception e) {
        attempts++;
        if (attempts >= maxAttempts) {
          throw e;
        }
        try {
          Thread.sleep(delay);
          delay *= 2;
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(ie);
        }
      }
    }
  }
}

