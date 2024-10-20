package org.kapil.concurrency.leetcode;

// Leetcode 1114
public class PrintInOrderWithLock {

  private final Boolean[] dones;

  public PrintInOrderWithLock() {
    dones = new Boolean[]{false, false};
  }


  public void first(Runnable printFirst) throws InterruptedException {
    printFirst.run();
    synchronized (dones) {
      dones[0] = true;
      dones.notifyAll();
    }
  }

  public void second(Runnable printSecond) throws InterruptedException {
    synchronized (dones) {
      while (!dones[0]) {
        dones.wait();
      }
      printSecond.run();
      dones[1] = true;
      dones.notifyAll();
    }
  }

  public void third(Runnable printThird) throws InterruptedException {
    synchronized (dones) {
      while (!dones[1]) {
        dones.wait();
      }
      printThird.run();
  }
}
  public static void main(String[] args) throws InterruptedException {
    PrintInOrderWithLock p = new PrintInOrderWithLock();
    p.first(() -> System.out.println("First"));
    p.second(() -> System.out.println("Second"));
    p.third(() -> System.out.println("Third"));
  }
}
