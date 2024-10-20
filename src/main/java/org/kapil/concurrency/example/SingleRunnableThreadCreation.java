package org.kapil.concurrency.example;

import java.lang.Thread.UncaughtExceptionHandler;

public class SingleRunnableThreadCreation {

  public static void main(String[] args) throws InterruptedException {
    Thread thread = new Thread(
        () -> {
          throw new RuntimeException("Intentional Exception");
        });
    thread.setName("New Worker Thread");
    /*thread.setPriority(Thread.MAX_PRIORITY);
    System.out.println(
        "We are in thread: " + Thread.currentThread().getName() + " before starting a new thread");
    thread.start();
    System.out.println(
        "We are in thread: " + Thread.currentThread().getName() + " after starting a new thread");*/
    thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        System.out.println("A critical error happened in thread: " + t.getName() + " the error is: " + e.getMessage());
      }
    });
    thread.start();

    Thread.sleep(10000);
  }

}