package org.kapil.concurrency.threadpercore;

/*
 * Copyright (c) 2023. Michael Pogrebinsky - Top Developer Academy
 * https://topdeveloperacademy.com
 * All rights reserved
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class IoBoundApplicationV2 {
  private static final int NUMBER_OF_TASKS = 10_000;

  public static void main(String[] args) {
    System.out.printf("Running %d tasks\n", NUMBER_OF_TASKS);

    long start = System.currentTimeMillis();
    performTasks();
    System.out.printf("Tasks took %dms to complete\n", System.currentTimeMillis() - start);
  }

  /**
   * Considering 1000 threads running 10_000 tasks
   * Each task running 100 blockingIO
   * Each blocking io of 1ms each
   * Total expected time = time per task = 1s (100 * 10ms = 1000ms = 1s)
   * 1000 Threads run 10000tasks = 10 * 1s = 10s but got 17,094s
   * This happens because each task is of 100 blocking IO ops with 99 context switches between them
   * The overhead of that context switch to block and unblock again, this, increase the
   * total amont of time Tasks took 16930ms to complete because of context switch
   *
   */
  private static void performTasks() {
    try (ExecutorService executorService = Executors.newFixedThreadPool(1000)) {
      IntStream.range(0, NUMBER_OF_TASKS)
          .forEach(i -> executorService.submit(() ->
              // Each task instead of running one blocking IO, will run 100.
              // We didn't change anything, each blocking IO is now 10ms, 100times * 10ms = 1sec
              // Tasks took 16930ms to complete because of context switch
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