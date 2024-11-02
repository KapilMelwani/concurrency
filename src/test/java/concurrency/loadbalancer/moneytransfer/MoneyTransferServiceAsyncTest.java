package concurrency.loadbalancer.moneytransfer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.kapil.concurrency.moneytransfer.application.service.MoneyTransferAsyncService;
import org.kapil.concurrency.moneytransfer.domain.model.Currency;
import org.kapil.concurrency.moneytransfer.domain.model.account.BankAccount;
import org.kapil.concurrency.moneytransfer.domain.model.account.Money;
import org.kapil.concurrency.moneytransfer.domain.model.transaction.TransactionStatus;
import org.kapil.concurrency.moneytransfer.domain.model.transaction.TransactionType;
import org.kapil.concurrency.moneytransfer.infrastructure.Journal;

class MoneyTransferServiceAsyncTest {

  private final Journal journalMock = mock(Journal.class);
  private final MoneyTransferAsyncService service = new MoneyTransferAsyncService(journalMock);

  @Test
  void testConcurrentTransfers() throws InterruptedException, ExecutionException {
    BankAccount fromAccount = new BankAccount("acc1",
        new Money(new BigDecimal("5000.00"), Currency.DOLLAR));
    BankAccount toAccount = new BankAccount("acc2",
        new Money(new BigDecimal("500.00"), Currency.DOLLAR));

    int numberOfTasks = 10000;
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    for (int i = 0; i < numberOfTasks; i++) {
      String requestId = "txn" + i;
      futures.add(service.transferCall(requestId, fromAccount, toAccount,
          new Money(new BigDecimal("0.05"), Currency.DOLLAR)));
    }
    CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    allOf.join();

    assertEquals(new BigDecimal("4500.00"), fromAccount.getBalance().getReference().getAmount());
    assertEquals(new BigDecimal("1000.00"), toAccount.getBalance().getReference().getAmount());
  }

  @Test
  void testConcurrentSameTransferRequests() throws InterruptedException {
    BankAccount fromAccount = new BankAccount("acc1", new Money(new BigDecimal(1000), Currency.DOLLAR));
    BankAccount toAccount = new BankAccount("acc2", new Money(new BigDecimal(500), Currency.DOLLAR));
    String requestId = "txnDuplicate";

    service.transferCall(requestId, fromAccount, toAccount, new Money(new BigDecimal(10), Currency.DOLLAR)).join();
    service.transferCall(requestId, fromAccount, toAccount, new Money(new BigDecimal(10), Currency.DOLLAR)).join();

    verify(journalMock, times(1)).recordTransaction(requestId, "acc1", new Money(new BigDecimal(10), Currency.DOLLAR),
        TransactionType.WITHDRAWAL, TransactionStatus.COMPLETED);
    verify(journalMock, times(1)).recordTransaction(requestId, "acc2", new Money(new BigDecimal(10), Currency.DOLLAR),
        TransactionType.DEPOSIT, TransactionStatus.COMPLETED);
    assertEquals(new BigDecimal("990.00"), fromAccount.getBalance().getReference().getAmount());
    assertEquals(new BigDecimal("510.00"), toAccount.getBalance().getReference().getAmount());
  }
}
