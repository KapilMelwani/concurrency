package org.kapil.concurrency.loadbalancer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class RoundRobinLoadBalancer implements LoadBalancer {
  private final List<Instance> instances;
  private final AtomicInteger index;
  private final Lock lock = new ReentrantLock();
  private final Condition instanceAvailable = lock.newCondition();

  public RoundRobinLoadBalancer(List<Instance> instances) {
    this.instances = new CopyOnWriteArrayList<>(instances);
    this.index = new AtomicInteger(0);  // Initialize the index to 0
  }

  @Override
  public Optional<Instance> nextInstance() throws InterruptedException {
    lock.lock();
    try {
      while (true) {
        if (instances.isEmpty()) {
          throw new IllegalStateException("No instances available");
        }

        int currentIndex = index.get() % instances.size();
        Instance selectedInstance = instances.get(currentIndex);

        if (selectedInstance.tryLock()) {
          index.incrementAndGet();
          return Optional.of(selectedInstance);
        } else {
          if (!instanceAvailable.await(50, TimeUnit.MILLISECONDS)) {
           // System.out.println("Timeout reached, no instance available.");
            return Optional.empty();
          }
        }
      }
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void releaseInstance(Instance instance) {
    lock.lock();
    try {
      instance.unlock();
      instanceAvailable.signalAll();  // Notify waiting threads that an instance is available
    } finally {
      lock.unlock();
    }
  }
}
