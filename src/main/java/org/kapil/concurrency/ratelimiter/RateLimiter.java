package org.kapil.concurrency.ratelimiter;

public interface RateLimiter {
  /**
   * Rate limits the code passed inside as an argument.
   *
   * @param code representation of the piece of code that needs to be rate limited.
   * @return true if executed, false otherwise.
   */
  boolean throttle(Code code);
  /**
   * When the piece of code that needs to be rate limited cannot be represented as a contiguous
   * code, then entry() should be used before we start executing the code. This brings the code inside the rate
   * limiting boundaries.
   *
   * @return true if the code will execute and false if rate limited.
   * <p
   */
  boolean acquire();
  /**
   * Allows multiple permits at the same time. If an expensive task takes n permits, the further calls should take the
   * toll on the rate.
   * @param permits Permits required.
   * @return true, if successful, false otherwise.
   */
  boolean acquire(int permits);

  /**
   * Interface to represent a contiguous piece of code that needs to be rate limited.
   */
  interface Code {
    void invoke();
  }
}
