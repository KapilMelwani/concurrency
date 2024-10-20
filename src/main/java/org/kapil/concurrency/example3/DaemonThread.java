package org.kapil.concurrency.example3;

import java.math.BigInteger;

/**
 * Daemon thread are background threads that do not prevent the application from
 * exiting if the main thread terminates. The JVM will terminate the daemon.
 * Scenario 1: It could be used for background tasks, that should not block our app from terminating
 * for example, File saving thread in a Text editor).
 * Scenario 2: Code in a worker thread is not under our control, and we don't want it to
 * block our app from terminating.
 */
public class DaemonThread {

  public static void main(String[] args) throws InterruptedException {
    Thread thread = new Thread(new LongComputationTask(new BigInteger("2"), new BigInteger("10000000")));
    thread.setDaemon(true);
    thread.start();
    Thread.sleep(1000);
    thread.interrupt();
  }

  private static class LongComputationTask implements Runnable {
    private BigInteger base;
    private BigInteger power;

    public LongComputationTask(BigInteger base, BigInteger power) {
      this.base = base;
      this.power = power;
    }

    @Override
    public void run() {
      System.out.println(base + "^" + power +  " = " + pow(base, power));
    }

    private BigInteger pow(BigInteger base, BigInteger power) {
      BigInteger result = BigInteger.ONE;
      for(BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0 ; i=i.add(BigInteger.ONE)) {
        result = result.multiply(base);
      }
      return result;
    }
  }

}
