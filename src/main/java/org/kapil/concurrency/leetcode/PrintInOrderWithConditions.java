package org.kapil.concurrency.leetcode;


// Leetcode 1114
public class PrintInOrderWithConditions {

  private boolean oneDone = false;
  private boolean twoDone = false;


  public PrintInOrderWithConditions() {
  }

  public synchronized void first(Runnable printFirst) throws InterruptedException {
      printFirst.run();
      oneDone = true;
      notifyAll();
  }

  public synchronized void second(Runnable printSecond) throws InterruptedException {
    while (!oneDone) {
      wait();
    }
    printSecond.run();
    twoDone = true;
    notifyAll();

  }

  public synchronized void third(Runnable printThird) throws InterruptedException {
    while (!twoDone) {
      wait();
    }
    printThird.run();
  }

  public static void main(String[] args) throws InterruptedException {
    PrintInOrderWithConditions p = new PrintInOrderWithConditions();
    p.first(() -> System.out.println("First"));
    p.second(() -> System.out.println("Second"));
    p.third(() -> System.out.println("Third"));
  }
}
