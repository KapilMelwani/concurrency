package org.kapil.concurrency.loadbalancer2;

import java.util.Collections;
import java.util.List;

public abstract class LoadBalancer {
    final List<String> ipList;

    public LoadBalancer(List <String> ipList) {
        this.ipList = Collections.unmodifiableList(ipList);
    }

    abstract String getIp();
}