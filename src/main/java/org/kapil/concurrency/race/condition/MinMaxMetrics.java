package org.kapil.concurrency.race.condition;

public class MinMaxMetrics {

  private volatile long currentMin;
  private volatile long currentMax;
  /**
   * Initializes all member variables
   */
  public MinMaxMetrics() {
    this.currentMin = Long.MAX_VALUE;
    this.currentMax = Long.MIN_VALUE;
  }

  /**
   * Adds a new sample to our metrics.
   */
  public void addSample(long newSample) {
    if(newSample > currentMax) {
      currentMax = newSample;
    } else if(newSample < currentMin) {
      currentMin = newSample;
    }
  }

  /**
   * Returns the smallest sample we've seen so far.
   */
  public long getMin() {
    return currentMin;
  }

  /**
   * Returns the biggest sample we've seen so far.
   */
  public long getMax() {
    return currentMax;
  }
}

