package org.kapil.concurrency.example3;

import java.math.BigInteger;
import java.util.List;

/**
 * Thread cordination with thread.join() to run different threads.
 * Why do we need to coordinate threads?
 * 1. Different threads run independently
 * 2. Order of execution is out of our control
 * Scenario 1: Thread A finishes entirely before Thread B
 * What if one thread depends on other thread? Thread A output is Thread B input.
 * - Naive Solution: Thread B runs in a loop and keeps checking if Thread A is done (extremely inefficient - Burn CPU cycles)
 * - Desired Solution: Thread B sleeps and when Thread A  is done, Thread B wakes up and starts processing. (Thread.join())
 */
public class FactorialThreadExample {

  public static void main(String[] args) {
    List<Long> inputNumbers = List.of(100000000L, 3435L, 2324L, 4656L, 23L, 5556L);

    List<FactorialThread> threads = inputNumbers.stream()
      .map(FactorialThread::new)
      .toList();

    threads.forEach(Thread::start);

    threads.forEach(thread -> {
      try {
        thread.join(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    for(int i=0;i<inputNumbers.size();i++) {
      FactorialThread thread = threads.get(i);
      if(thread.isFinished()) {
        System.out.println("Factorial of " + inputNumbers.get(i) + " is " + thread.getResult());
      } else {
        System.out.println("The calculation for " + inputNumbers.get(i) + " is still in progress.");
      }
    }
  }

  private static class FactorialThread extends Thread {
    private final long inputNumber;
    private BigInteger result;
    private boolean finished;

    public FactorialThread(long inputNumber) {
      this.inputNumber = inputNumber;
      this.finished = false;
    }

    @Override
    public void run() {
      this.result = calculateFactorial(inputNumber);
      this.finished = true;
    }

    private BigInteger calculateFactorial(long inputNumber) {
      BigInteger result = BigInteger.ONE;
      for(long i = inputNumber; i > 0; i--) {
        result = result.multiply(new BigInteger(Long.toString(i)));
      }
      return result;
    }

    public BigInteger getResult() {
      return result;
    }

    public boolean isFinished() {
      return finished;
    }
  }
}
