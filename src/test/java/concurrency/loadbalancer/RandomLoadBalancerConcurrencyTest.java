package concurrency.loadbalancer;

import org.junit.jupiter.api.Test;
import org.kapil.concurrency.loadbalancer.Instance;
import org.kapil.concurrency.loadbalancer.LoadBalancer;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.kapil.concurrency.loadbalancer.RandomLoadBalancer;

public class RandomLoadBalancerConcurrencyTest {

  @Test
  public void testConcurrentAccess() throws InterruptedException {
    Instance instance1 = new Instance("192.168.0.1");
    Instance instance2 = new Instance("192.168.0.2");
    LoadBalancer loadBalancer = new RandomLoadBalancer(List.of(instance1, instance2));

    Runnable task = () -> {
      try {
        Optional<Instance> instance = loadBalancer.nextInstance();
        if (instance.isPresent()) {
          Thread.sleep(100);
          loadBalancer.releaseInstance(instance.get());
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };

    Thread[] threads = new Thread[50];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(task);
      threads[i].start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    assertTrue(instance1.isFree());
    assertTrue(instance2.isFree());
  }
}
