package org.kapil.concurrency.loadbalancer;

import java.util.concurrent.Semaphore;

public class Instance {

  private static final int MAX_AVAILABLE = 4;

  private final String ip;
  private final Semaphore semaphore = new Semaphore(MAX_AVAILABLE);

  public Instance(String ip) {
    this.ip = ip;
  }

  public String getIp() {
    return ip;
  }

  public boolean tryLock() {
   return semaphore.tryAcquire();
  }

  public void unlock() {
    semaphore.release();
  }

  public Boolean isFree() {
    return semaphore.availablePermits() == MAX_AVAILABLE;
  }
}
