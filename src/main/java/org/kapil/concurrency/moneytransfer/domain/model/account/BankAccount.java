package org.kapil.concurrency.moneytransfer.domain.model.account;

import java.util.concurrent.atomic.AtomicStampedReference;
import lombok.Getter;
import org.kapil.concurrency.moneytransfer.domain.exception.InsufficientFundsException;

public class BankAccount {
  @Getter
  private final String accountId;
  @Getter
  private final AtomicStampedReference<Money> balance;


  public BankAccount(String id, Money balance) {
    this.accountId = id;
    this.balance = new AtomicStampedReference<>(balance, 0);
  }

  public Money withdraw(Money money) {
    if (!validation(money)) {
      throw new IllegalArgumentException("Cannot withdraw using different currency");
    }

    Money currentBalance;
    int currentStamp;

    do {
      currentBalance = balance.getReference();
      currentStamp = balance.getStamp();

      if (currentBalance.compareTo(money) < 0) {
        throw new InsufficientFundsException("Not enough funds.");
      }

      Money newBalance = currentBalance.subtract(money);
      if (balance.compareAndSet(currentBalance, newBalance, currentStamp, currentStamp + 1)) {
        return balance.getReference();
      }
    } while (true);
  }

  public Money deposit(Money amount) {
    if (!validation(amount)) {
      throw new IllegalArgumentException("Cannot deposit using different currency");
    }

    Money currentBalance;
    int currentStamp;
    do {
      currentBalance = balance.getReference();
      currentStamp = balance.getStamp();

      Money newBalance = currentBalance.add(amount);
      if (balance.compareAndSet(currentBalance, newBalance, currentStamp, currentStamp + 1)) {
        return balance.getReference();
      }
    } while (true);
  }


  private boolean validation(Money money) {
    return balance.getReference().getCurrency().equals(money.getCurrency());
  }
}
