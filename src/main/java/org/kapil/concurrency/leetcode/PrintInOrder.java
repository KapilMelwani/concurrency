package org.kapil.concurrency.leetcode;

import java.util.concurrent.Semaphore;

// Leetcode 1114
public class PrintInOrder {

  Semaphore run1 = new Semaphore(0);
  Semaphore run2 = new Semaphore(0);


  public PrintInOrder() {
  }

  public void first(Runnable printFirst) throws InterruptedException {
    try {
      printFirst.run();
    } finally {
      run1.release();
    }
  }

  public void second(Runnable printSecond) throws InterruptedException {
    run1.acquire();
    try {
      printSecond.run();
    } finally {
      run2.release();
    }
  }

  public void third(Runnable printThird) throws InterruptedException {
    run2.acquire();
    printThird.run();
  }

  public static void main(String[] args) throws InterruptedException {
    PrintInOrder p = new PrintInOrder();
    p.first(() -> System.out.println("First"));
    p.second(() -> System.out.println("Second"));
    p.third(() -> System.out.println("Third"));
  }
}
