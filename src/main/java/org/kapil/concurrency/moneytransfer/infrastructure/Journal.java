package org.kapil.concurrency.moneytransfer.infrastructure;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;
import org.kapil.concurrency.moneytransfer.domain.model.transaction.TransactionStatus;
import org.kapil.concurrency.moneytransfer.domain.model.account.Money;
import org.kapil.concurrency.moneytransfer.domain.model.transaction.TransactionRecord;
import org.kapil.concurrency.moneytransfer.domain.model.transaction.TransactionType;

@Getter
public class Journal {


    private final ConcurrentLinkedQueue<TransactionRecord> transactions = new ConcurrentLinkedQueue<>();

    public void recordTransaction(String requestId, String accountId, Money money, TransactionType type, TransactionStatus status) {
        TransactionRecord record = new TransactionRecord(requestId, accountId, money, type, status);
        transactions.add(record);
        System.out.println("Journal: Recorded transaction " + record);
    }

    public Optional<TransactionRecord> getTransaction(String requestId) {
        return transactions.stream()
            .filter(record -> record.getRequestId().equals(requestId))
            .findFirst();
    }
}