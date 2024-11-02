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
import org.kapil.concurrency.moneytransfer.infrastructure.IdempotencyManager;
import org.kapil.concurrency.moneytransfer.infrastructure.Journal;

public class MoneyTransferService {

  private final Journal journal;
  private final IdempotencyManager idempotencyManager;

  public MoneyTransferService(IdempotencyManager idempotencyManager, Journal journal) {
    this.idempotencyManager = idempotencyManager;
    this.journal = journal;
  }


  public boolean transfer(String requestId, BankAccount fromAccount, BankAccount toAccount,
      Money amount) {
    if (idempotencyManager.isProcessed(requestId)) {
      System.out.println("Transaction already processed: " + requestId);
      return false;
    }
    try {
      fromAccount.withdraw(amount);
      journal.recordTransaction(requestId, fromAccount.getAccountId(), amount, TransactionType.WITHDRAWAL,
          TransactionStatus.COMPLETED);
      toAccount.deposit(amount);
      journal.recordTransaction(requestId, toAccount.getAccountId(), amount, TransactionType.DEPOSIT,
          TransactionStatus.COMPLETED);
      idempotencyManager.markAsProcessed(requestId);
      return true;
    } catch (Exception e) {
      System.out.println("Transaction failed: " + requestId);
      return false;
    }
  }
}
