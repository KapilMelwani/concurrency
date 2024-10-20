package org.kapil.concurrency.moneytransfer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IdempotencyManager {
  private final ConcurrentHashMap<String, Boolean> idempotencyMap = new ConcurrentHashMap<>();
  private static final long TTL = 1000;
  private final ScheduledExecutorService scheduledExecutorService;

  public IdempotencyManager() {
    scheduledExecutorService = Executors.newScheduledThreadPool(1);
    scheduledExecutorService.scheduleAtFixedRate(this::cleanUp, TTL, TTL, TimeUnit.MILLISECONDS);
  }

  public boolean isProcessed(String requestId) {
    return idempotencyMap.containsKey(requestId);
  }

  public void markAsProcessed(String requestId) {
    idempotencyMap.put(requestId, true);
  }

  private void cleanUp() {
    idempotencyMap.entrySet().removeIf(entry -> entry.getValue());
  }
}
