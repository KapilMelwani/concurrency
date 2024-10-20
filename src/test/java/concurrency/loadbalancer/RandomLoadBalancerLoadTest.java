package concurrency.loadbalancer;

import java.util.concurrent.atomic.AtomicInteger;
import org.kapil.concurrency.loadbalancer.Instance;
import org.kapil.concurrency.loadbalancer.LoadBalancer;
import org.kapil.concurrency.loadbalancer.RandomLoadBalancer;
import java.util.List;
import java.util.Optional;

public class RandomLoadBalancerLoadTest {
    public static void main(String[] args) throws InterruptedException {
      Instance instance1 = new Instance("192.168.0.1");
      LoadBalancer loadBalancer = new RandomLoadBalancer(List.of(instance1));

      int totalRequests = 1000;
      AtomicInteger successfulRequests = new AtomicInteger();
      AtomicInteger failedRequests = new AtomicInteger();

      Runnable clientTask = () -> {
        try {
          Optional<Instance> instance = loadBalancer.nextInstance();
          if (instance.isPresent()) {
            Thread.sleep(50);
            loadBalancer.releaseInstance(instance.get());
            successfulRequests.incrementAndGet();
          } else {
            failedRequests.incrementAndGet();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      };

      Thread[] clientThreads = new Thread[totalRequests];
      for (int i = 0; i < totalRequests; i++) {
        clientThreads[i] = new Thread(clientTask);
        clientThreads[i].start();
      }

      for (Thread thread : clientThreads) {
        thread.join();
      }

      System.out.println("Successful: " + successfulRequests.get());
      System.out.println("Failed: " + failedRequests.get());
    }
}
