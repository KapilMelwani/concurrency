package org.kapil.concurrency.loadbalancer2;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WeightedRoundRobinLoadBalancer extends RoundRobinLoadBalancer {

  /*
    "192.168.0.1", 6) -> A
    "192.168.0.2", 6) -> B
    "192.168.0.3", 3) -> C
    List will be [A, A, A, A, A, A, B, B, B, B, B, B, C, C, C]
   */
    public WeightedRoundRobinLoadBalancer(Map<String, Integer> ipMap) {
        super(
            ipMap.keySet()
                .stream()
                .map(ip -> {
                    List<String> tempList =  new LinkedList<>();
                    ipMap.forEach((key, value) -> {
                        for (int i=0; i<value; i++) {
                            tempList.add(key);
                        }
                    });
                    return tempList;
                })
                .flatMap(Collection::stream)
                .toList()
        );
    }
}