package org.kapil.concurrency.moneytransfer.domain.model.account;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import org.kapil.concurrency.moneytransfer.domain.exception.InsufficientFundsException;

public class BankAccount {
  @Getter
  private final String accountId;
  @Getter
  private Money balance;
  private final AtomicInteger version;

  public BankAccount(String id, Money balance) {
    this.accountId = id;
    this.balance = balance;
    this.version = new AtomicInteger(0);
  }

  public Money withdraw(Money money) {
    if (!validation(money)) {
      throw new IllegalArgumentException("Cannot deposit using different currency");
    }

    Money currentBalance;
    int currentVersion;
    do {
      currentVersion = version.get();
      currentBalance = balance;
      if (currentBalance.compareTo(money) < 0) {
        throw new InsufficientFundsException("Not funds.");
      }
      this.balance = currentBalance.subtract(money);
    } while (!version.compareAndSet(currentVersion, currentVersion + 1));

    return this.balance;
  }

  public Money deposit(Money amount) {
    if (!validation(amount)) {
      throw new IllegalArgumentException("Cannot deposit using different currency");
    }

    Money currentBalance;
    int currentVersion;
    do {
      currentVersion = version.get();
      currentBalance = balance;
      this.balance = currentBalance.add(amount);
    } while (!version.compareAndSet(currentVersion, currentVersion + 1));
    return this.balance;
  }

  private boolean validation(Money money) {
    return balance.getCurrency().equals(money.getCurrency());
  }
}
