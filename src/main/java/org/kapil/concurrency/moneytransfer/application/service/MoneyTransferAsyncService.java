package org.kapil.concurrency.moneytransfer.application.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.kapil.concurrency.moneytransfer.domain.model.account.BankAccount;
import org.kapil.concurrency.moneytransfer.domain.model.account.Money;
import org.kapil.concurrency.moneytransfer.domain.model.transaction.TransactionStatus;
import org.kapil.concurrency.moneytransfer.domain.model.transaction.TransactionType;
import org.kapil.concurrency.moneytransfer.infrastructure.Journal;

public class MoneyTransferAsyncService {

  private final Cache<String, CompletableFuture<Void>> resultCache;
  private final Journal journal;
  private ExecutorService processingExecutor;

  public MoneyTransferAsyncService(Journal journal) {
    this.resultCache = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.SECONDS)
        .build();
    this.processingExecutor = Executors.newFixedThreadPool(20, threadFactoryWithPrefix("processing-"));
    this.journal = journal;
  }

  public CompletableFuture<Void> transferCall(String requestId, BankAccount fromAccount, BankAccount toAccount,
      Money amount) {
    try {
      return resultCache.get(requestId, () -> {
        return CompletableFuture.runAsync(() -> transfer(requestId, fromAccount, toAccount, amount), processingExecutor);
      });
    } catch (ExecutionException e) {
      return CompletableFuture.failedFuture(e);
    }
  }


  public void transfer(String requestId, BankAccount fromAccount, BankAccount toAccount,
      Money amount) {
    fromAccount.withdraw(amount);
    journal.recordTransaction(requestId, fromAccount.getAccountId(), amount,
        TransactionType.WITHDRAWAL,
        TransactionStatus.COMPLETED);
    toAccount.deposit(amount);
    journal.recordTransaction(requestId, toAccount.getAccountId(), amount, TransactionType.DEPOSIT,
        TransactionStatus.COMPLETED);
  }

  private static ThreadFactory threadFactoryWithPrefix(String prefix) {
    return new ThreadFactory() {
      private final AtomicInteger counter = new AtomicInteger();

      @Override
      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(prefix + counter.getAndIncrement());
        return thread;
      }
    };
  }
}
