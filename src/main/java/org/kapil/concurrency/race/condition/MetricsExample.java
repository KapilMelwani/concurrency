package org.kapil.concurrency.race.condition;

import java.util.Random;

public class MetricsExample {

  public static void main(String[] args) {
    Metrics metrics = new Metrics();

    BusinessLogic businessLogicThread1 = new BusinessLogic(metrics);

    BusinessLogic businessLogicThread2 = new BusinessLogic(metrics);

    MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);

    businessLogicThread1.start();
    businessLogicThread2.start();
    metricsPrinter.start();
  }

  public static class MetricsPrinter extends Thread {

    private Metrics metrics;

    public MetricsPrinter(Metrics metrics) {
      this.metrics = metrics;
    }

    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        double currentAverage = metrics.getAverage();

        System.out.println("Current Average is " + currentAverage);
      }
    }
  }

  public static class BusinessLogic extends Thread {

    private Metrics metrics;
    private Random random = new Random();

    public BusinessLogic(Metrics metrics) {
      this.metrics = metrics;
    }

    @Override
    public void run() {
      while (true) {
        long start = System.currentTimeMillis();

        try {
          Thread.sleep(random.nextInt(10));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        metrics.addSample(end - start);
      }
    }
  }

  public static class Metrics {

    private long count = 0;
    private volatile double average = 0.0; // Adding volatile to guarantee that average op is atomic and the getter read is also atomic

    /**
     * Need synchronized because count and average will be shared by multiple threads
     * because multiple threads shares the Metrics object.
     * @param sample
     */
    public synchronized void addSample(long sample) {
      double currentSum = average * count;
      count++;
      average = (currentSum + sample) / count;
    }

    public double getAverage() { // Getter is thread safe
      return average;
    }
  }
}
