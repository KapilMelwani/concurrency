package org.kapil.concurrency.loadbalancer;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
public class Main {

  private static final AtomicInteger processedRequests = new AtomicInteger(0);
  private static final AtomicInteger failedRequests = new AtomicInteger(0);
  private static final AtomicLong totalProcessingTime = new AtomicLong(0);
  private static final Integer DUMMY_TASK_DURATION = 500;
  private static final HashMap<String, Integer> map = new HashMap<>();
  public static void main(String[] args) throws InterruptedException {
    Instance instance1 = new Instance("192.168.0.1");
    Instance instance2 = new Instance("192.168.0.2");
    Instance instance3 = new Instance("192.168.0.3");
    Instance instance4 = new Instance("192.168.0.4");

    map.put(instance1.getIp(), 0);
    map.put(instance2.getIp(), 0);
    map.put(instance3.getIp(), 0);
    map.put(instance4.getIp(), 0);


    /**
     * --- Load Testing Complete ---
     * Total Requests Processed: 1000
     * Failed Requests (No instance available): 0
     * Total Processing Time: 500_000 ms
     * Total Duration (Test Duration): 50434 ms
     *
     * --- Load Testing Complete ---
     * Total Requests Processed: 878
     * Failed Requests (No instance available): 122
     * Total Processing Time: 439000 ms
     * Total Duration (Test Duration): 45854 ms
     */
    //LoadBalancer lb = new RoundRobinLoadBalancer(List.of(instance1, instance2, instance3, instance4));
    LoadBalancer lb = new RandomLoadBalancer(List.of(instance1, instance2, instance3, instance4));

    long startTime = System.currentTimeMillis();

    try (ExecutorService executor = Executors.newCachedThreadPool()) {
      for (int i = 0; i < 1000; i++) {
        executor.submit(() -> {
          try {
            while (true) {
              Optional<Instance> instance = lb.nextInstance();
              if (instance.isPresent()) {
                //System.out.println("Processing request on instance: " + instance.get().getIp());
                dummyTask();
                totalProcessingTime.addAndGet(DUMMY_TASK_DURATION);
                map.put(instance.get().getIp(), map.get(instance.get().getIp()) + 1);
                processedRequests.incrementAndGet();
                lb.releaseInstance(instance.get());
                break;
              } else {
                failedRequests.incrementAndGet();
              }
            }
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        });
      }
    }

    long endTime = System.currentTimeMillis();
    long totalDuration = endTime - startTime;
    System.out.println("\n--- Load Testing Complete ---");
    System.out.println("Total Requests Processed: " + processedRequests.get());
    System.out.println("Failed Requests (No instance available): " + failedRequests.get());
    System.out.println("Total Processing Time: " + totalProcessingTime.get() + " ms");
    System.out.println("Total Duration (Test Duration): " + totalDuration + " ms");
    map.forEach((k, v) -> System.out.println(k + " : " + v));
  }

  public static void dummyTask() throws InterruptedException {
    Thread.sleep(DUMMY_TASK_DURATION);
   // System.out.println("Task completed.");
  }
}