package org.kapil.concurrency.loadbalancer;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// RandomLB with Instances that allows max 4 connections at a time
public class RandomLoadBalancer implements LoadBalancer {

  private final List<Instance> instances;
  private final Lock lock = new ReentrantLock();
  private final Condition instanceAvailable = lock.newCondition();

  public RandomLoadBalancer(List<Instance> instances) {
    this.instances = new CopyOnWriteArrayList<>(instances);
  }

  @Override
  public Optional<Instance> nextInstance() throws InterruptedException {
    lock.lock();
    try {
      while (true) {
        if (instances.isEmpty()) {
          throw new IllegalStateException("No instances available");
        }

        Instance selectedInstance = instances.get(new Random().nextInt(instances.size()));
        if (selectedInstance.tryLock()) {
          return Optional.of(selectedInstance);
        }

        if(!instanceAvailable.await(100, TimeUnit.MILLISECONDS)) {
          return Optional.empty();
        }
      }
    } finally {
      lock.unlock();
    }
  }

  public void releaseInstance(Instance instance) {
    try {
      instance.unlock();
      instanceAvailable.signalAll();
    } finally {
      lock.unlock();
    }
  }
}