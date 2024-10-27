package org.kapil.concurrency.moneytransfer.domain.model.transaction;

import lombok.Getter;
import org.kapil.concurrency.moneytransfer.domain.model.account.Money;

@Getter
public class TransactionRecord {
    private final String requestId;
    private final String fromAccountId;
    private final Money amount;
    private final TransactionType type;
    private final TransactionStatus status;

    public TransactionRecord(String requestId, String fromAccountId, Money amount, TransactionType type, TransactionStatus status) {
        this.requestId = requestId;
        this.fromAccountId = fromAccountId;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "requestId='" + requestId + '\'' +
                ", fromAccountId='" + fromAccountId + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}