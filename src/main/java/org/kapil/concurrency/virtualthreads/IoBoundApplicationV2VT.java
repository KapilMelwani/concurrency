package org.kapil.concurrency.virtualthreads;

/*
 * Copyright (c) 2023. Michael Pogrebinsky - Top Developer Academy
 * https://topdeveloperacademy.com
 * All rights reserved
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class IoBoundApplicationV2VT {
  private static final int NUMBER_OF_TASKS = 10_000;

  public static void main(String[] args) {
    System.out.printf("Running %d tasks\n", NUMBER_OF_TASKS);

    long start = System.currentTimeMillis();
    performTasks();
    System.out.printf("Tasks took %dms to complete\n", System.currentTimeMillis() - start);
  }

  /**
   * With VT took 9395ms to complete
   */
  private static void performTasks() {
    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      IntStream.range(0, NUMBER_OF_TASKS)
          .forEach(i -> executorService.submit(() ->
              // Each task instead of running one blocking IO, will run 100.
              // We didn't change anything, each blocking IO is now 10ms, 100times * 10ms = 1sec
              IntStream.range(0, 100).forEach(j -> blockingIoOperation())
          ));
    }
  }

  // Simulates a long blocking IO
  private static void blockingIoOperation() {
    System.out.println("Executing a blocking task from thread: " + Thread.currentThread());
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}