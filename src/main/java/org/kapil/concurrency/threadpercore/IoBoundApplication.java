package org.kapil.concurrency.threadpercore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoBoundApplication {

  private static final int NUMBER_OF_TASKS = 1000; // Fails with 10_000
  public static void main(String[] args) {
    System.out.printf("Running %d tasks\n", NUMBER_OF_TASKS);
    long start = System.currentTimeMillis();
    performTasks();

    System.out.printf("Tasks took %dms to complete\n", System.currentTimeMillis() - start);

  }

  private static void performTasks() {
    /*
    Dynamic Thread pool will grow to us many threads we need to complete these tasks, cache those
    threads to reuse them in a future.
     */
    try(ExecutorService executorService = Executors.newCachedThreadPool()) {
      for(int i=0;i<NUMBER_OF_TASKS;i++){
        executorService.submit(IoBoundApplication::blockingIoOperation);
      }
    }
  }

  private static void blockingIoOperation() {
    System.out.println("Executing a blocking task from thread: " + Thread.currentThread());
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}