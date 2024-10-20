package org.kapil.concurrency.loadbalancer2;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RoundRobinLoadBalancer extends LoadBalancer {

    private int counter = 0;
    private final ReentrantLock lock;

    public RoundRobinLoadBalancer(List<String> list) {
        super(list);
        lock = new ReentrantLock();
    }

    @Override
    public String getIp() {
        lock.lock();
        try {
            String ip = ipList.get(counter);
            counter += 1;
            if (counter == ipList.size()) {
                counter = 0;
            }
            return ip;
        } finally {
            lock.unlock();
        }
    }
}