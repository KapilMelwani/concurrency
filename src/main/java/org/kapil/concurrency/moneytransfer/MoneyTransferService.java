package org.kapil.concurrency.moneytransfer;

import java.math.BigDecimal;

public class MoneyTransferService {
  private final IdempotencyManager idempotencyManager;

  public MoneyTransferService(IdempotencyManager idempotencyManager) {
    this.idempotencyManager = idempotencyManager;
  }

  public boolean transfer(String requestId, BankAccount fromAccount, BankAccount toAccount, BigDecimal amount) {
    if (idempotencyManager.isProcessed(requestId)) {
      System.out.println("Transfer already processed: " + requestId);
      return false;
    }

    try {
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        idempotencyManager.markAsProcessed(requestId);
        System.out.println("Transfer (" + requestId + "): successful: " + amount + " from " + fromAccount.getAccountId() + " to " + toAccount.getAccountId());
        return true;
    } catch (InsufficientFundsException e) {
      System.out.println("Transfer (" + requestId + "): Insufficient funds. Try again.");
      return false;
    }
  }
}
