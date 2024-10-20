package org.kapil.concurrency.moneytransfer;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;

public class BankAccount {
  @Getter
  private final String accountId;
  private AtomicReference<BigDecimal> balance;

  public BankAccount(String id, BigDecimal balance) {
    this.accountId = id;
    this.balance = new AtomicReference<>(balance);
  }

  public void withdraw(BigDecimal amount) {
    BigDecimal currentBalance;
    BigDecimal newBalance;
    do {
      currentBalance = balance.get();
      if (currentBalance.compareTo(amount) < 0) {
        throw new InsufficientFundsException("Not funds.");
      }
      newBalance = currentBalance.subtract(amount);
    } while (!balance.compareAndSet(currentBalance, newBalance));
  }

  public void deposit(BigDecimal amount) {
    BigDecimal currentBalance;
    BigDecimal newBalance;
    do {
      currentBalance = balance.get();
      newBalance = currentBalance.add(amount);
    } while (!balance.compareAndSet(currentBalance, newBalance));
  }

  public BigDecimal getBalance() {
    return balance.get();
  }
}
