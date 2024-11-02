package concurrency.loadbalancer.moneytransfer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kapil.concurrency.moneytransfer.application.service.MoneyTransferService;
import org.kapil.concurrency.moneytransfer.domain.model.Currency;
import org.kapil.concurrency.moneytransfer.domain.model.account.BankAccount;
import org.kapil.concurrency.moneytransfer.domain.model.account.Money;
import org.kapil.concurrency.moneytransfer.infrastructure.IdempotencyManager;
import org.kapil.concurrency.moneytransfer.infrastructure.Journal;

class MoneyTransferServiceTest2 {

 /*   private MoneyTransferService service;
    private Journal journalMock;
    private BankAccount fromAccount;
    private BankAccount toAccount;

    @BeforeEach
    void setUp() {
        journalMock = mock(Journal.class);
        service = new MoneyTransferService(journalMock);
        fromAccount = new BankAccount("acc1", new Money(1000, Currency.DOLLAR));
        toAccount = new BankAccount("acc2", new Money(500));
    }

    @Test
    void transferShouldCompleteSuccessfully() throws ExecutionException, InterruptedException {
        Money amount = new Money(100);
        String requestId = "txn1";

        CompletableFuture<Void> result = service.transferCall(requestId, fromAccount, toAccount, amount);
        result.get(); // Esperar a que la operación termine

        // Verificar que se registraron las transacciones en el journal
        verify(journalMock).recordTransaction(requestId, "acc1", amount, TransactionType.WITHDRAWAL, TransactionStatus.COMPLETED);
        verify(journalMock).recordTransaction(requestId, "acc2", amount, TransactionType.DEPOSIT, TransactionStatus.COMPLETED);
    }

    @Test
    void transferShouldBeIdempotent() throws ExecutionException, InterruptedException {
        Money amount = new Money(100);
        String requestId = "txn1";

        service.transferCall(requestId, fromAccount, toAccount, amount).get();
        service.transferCall(requestId, fromAccount, toAccount, amount).get();

        // Verificar que las transacciones no se registraron nuevamente
        verify(journalMock, times(1)).recordTransaction(requestId, "acc1", amount, TransactionType.WITHDRAWAL, TransactionStatus.COMPLETED);
        verify(journalMock, times(1)).recordTransaction(requestId, "acc2", amount, TransactionType.DEPOSIT, TransactionStatus.COMPLETED);
    }*/
}
