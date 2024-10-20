package org.kapil.concurrency.urlshortener.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyGenerator extends Engine {

  private AtomicInteger sequence = new AtomicInteger(0);
  private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private final int machineId;
  private final int datacenterId;
  private static final Set<String> generatedKeys = ConcurrentHashMap.newKeySet();  // Thread-safe HashSet

  public KeyGenerator(int machineId, int datacenterId) {
    this.machineId = machineId;
    this.datacenterId = datacenterId;
  }

  private String base62(long time, int machineId, int datacenterId, int sequence) {
    StringBuilder encodedString = new StringBuilder();
    long modifiedNumber = Long.parseLong(String.format("%d%d%d%d", time, machineId, datacenterId, sequence));
    while (modifiedNumber > 0) {
      int remainder = (int) (modifiedNumber % 62);
      encodedString.append(BASE62.charAt(remainder));
      modifiedNumber /= 62;
    }
    return encodedString.reverse().toString();
  }

  @Override
  public String getUniqueKey(String url) {
    long currentTime = System.currentTimeMillis();
    int currentSequence = sequence.getAndIncrement();
    String key;
    do {
      key = base62(currentTime, machineId, datacenterId, currentSequence);
    } while (!generatedKeys.add(key));
    return key;
  }

  public void cleanUpOldKeys(long expirationTimeMillis) {
    long currentTime = System.currentTimeMillis();
    generatedKeys.removeIf(key -> {
      long keyTime = extractTimeFromKey(key);
      return (currentTime - keyTime) > expirationTimeMillis;
    });
  }

  private long extractTimeFromKey(String key) {
    long time = 0;
    for (int i = 0; i < key.length(); i++) {
      time = time * 62 + BASE62.indexOf(key.charAt(i));
    }
    return time / 1000000;
  }
}
