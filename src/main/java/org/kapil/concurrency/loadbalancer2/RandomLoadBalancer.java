package org.kapil.concurrency.loadbalancer2;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalancer extends LoadBalancer {

    public RandomLoadBalancer(List<String> ipList) {
        super(ipList);
    }

    @Override
    public String getIp() {
        return ipList.get(ThreadLocalRandom.current().nextInt(ipList.size()));
    }
}