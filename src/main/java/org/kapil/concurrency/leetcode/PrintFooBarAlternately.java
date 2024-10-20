package org.kapil.concurrency.leetcode;

import java.util.concurrent.Semaphore;

// Leetcode 1115
public class PrintFooBarAlternately {
  private final int n;

  Semaphore fooS = new Semaphore(1);
  Semaphore barS = new Semaphore(0);

  public PrintFooBarAlternately(int n) {
    this.n = n;
  }

  public void foo(Runnable printFoo) throws InterruptedException {
    for (int i = 0; i < n; i++) {
      fooS.acquire();
      printFoo.run();
      barS.release();
    }
  }

  public void bar(Runnable printBar) throws InterruptedException {
    for (int i = 0; i < n; i++) {
      barS.acquire();
      printBar.run();
      fooS.release();
    }
  }
}
