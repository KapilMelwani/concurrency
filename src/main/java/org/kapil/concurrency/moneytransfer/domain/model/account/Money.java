package org.kapil.concurrency.moneytransfer.domain.model.account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.kapil.concurrency.moneytransfer.domain.model.Currency;

public class Money implements Comparable<Money> {
  private static final int SCALE = 2;
  private final BigDecimal amount;
  private final Currency currency;

  public Money(BigDecimal amount, Currency currency) {
    this.amount = amount.setScale(SCALE, RoundingMode.HALF_EVEN);
    this.currency = currency;
  }

  public Money add(Money other) {
    if (!currency.equals(other.currency)) {
      throw new RuntimeException("Currencies do not match.");
    }
    return new Money(this.amount.add(other.getAmount()), this.currency);
  }

  public Money subtract(Money other) {
    if (!currency.equals(other.currency)) {
      throw new RuntimeException("Currencies do not match.");
    }
    return new Money(this.amount.subtract(other.getAmount()), this.currency);
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  @Override
  public int hashCode() {
    return amount.hashCode() + currency.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Money money = (Money) obj;
    return amount.equals(money.getAmount()) && currency.equals(money.getCurrency());
  }

  @Override
  public int compareTo(Money o) {
    if (!this.currency.equals(o.getCurrency())) {
      throw new IllegalArgumentException("Cannot compare Money with different currencies");
    }
    return this.getAmount().compareTo(o.getAmount());
  }

  @Override
  public String toString() {
    return amount + " " + currency.toString();
  }
}
