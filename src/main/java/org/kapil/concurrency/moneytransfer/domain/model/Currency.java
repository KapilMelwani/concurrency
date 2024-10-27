package org.kapil.concurrency.moneytransfer.domain.model;

public enum Currency {
  EURO("€"),
  DOLLAR("$");

  private final String symbol;

  Currency(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return this.symbol;
  }
}
