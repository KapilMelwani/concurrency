package org.kapil.concurrency.loadbalancer;

import java.util.Optional;

public interface LoadBalancer {
  Optional<Instance> nextInstance() throws InterruptedException;
  void releaseInstance(Instance instance);
}
