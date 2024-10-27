package concurrency.loadbalancer.moneytransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.kapil.concurrency.moneytransfer.application.service.MoneyTransferService;
import org.kapil.concurrency.moneytransfer.domain.model.account.BankAccount;
import org.kapil.concurrency.moneytransfer.domain.model.Currency;
import org.kapil.concurrency.moneytransfer.domain.model.account.Money;
import org.kapil.concurrency.moneytransfer.infrastructure.IdempotencyManager;
import org.kapil.concurrency.moneytransfer.infrastructure.Journal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTransferServiceTest {

    private BankAccount accountA;
    private BankAccount accountB;
    private MoneyTransferService transferService;
    private IdempotencyManager idempotencyManager;
    private Journal journal;

    @BeforeEach
    void setup() {
        accountA = new BankAccount("AccountA", new Money(new BigDecimal("1000.00"), Currency.DOLLAR));
        accountB = new BankAccount("AccountB", new Money(new BigDecimal("1000.00"), Currency.DOLLAR));
        idempotencyManager = new IdempotencyManager();
        journal = new Journal();
        transferService = new MoneyTransferService(idempotencyManager, journal);
    }

    @Test
    void testConcurrentWithdrawals() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final String requestId = "withdrawal-request-" + i;
            executor.submit(() -> {
                transferService.transfer(requestId, accountA, accountB, new Money(new BigDecimal("100.00"), Currency.DOLLAR));
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(new BigDecimal("0.00"), accountA.getBalance().getAmount());
        assertEquals(new BigDecimal("2000.00"), accountB.getBalance().getAmount());
    }

    @Test
    void testConcurrentDeposits() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final String requestId = "deposit-request-" + i;
            executor.submit(() -> {
                transferService.transfer(requestId, accountA, accountB, new Money(new BigDecimal("100.00"), Currency.DOLLAR));
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MILLISECONDS);

        assertEquals(new BigDecimal("0.00"), accountA.getBalance().getAmount());
        assertEquals(new BigDecimal("2000.00"), accountB.getBalance().getAmount());
    }

    @Test
    void testMixedConcurrentTransfers() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final String requestId = "transfer-request-" + i;
            if (i % 2 == 0) {
                transferService.transfer(requestId, accountA, accountB, new Money(new BigDecimal("50.00"), Currency.DOLLAR));
            } else {
                transferService.transfer(requestId, accountB, accountA, new Money(new BigDecimal("50.00"), Currency.DOLLAR));
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        BigDecimal expectedBalanceA = new BigDecimal("1000.00");
        BigDecimal expectedBalanceB = new BigDecimal("1000.00");

        assertEquals(expectedBalanceA, accountA.getBalance().getAmount());
        assertEquals(expectedBalanceB, accountB.getBalance().getAmount());
    }

    @Test
    void testInsufficientFunds() {
        String requestId = "insufficient-funds-request";
        transferService.transfer(requestId, accountA, accountB, new Money(new BigDecimal("1001.00"), Currency.DOLLAR));

        assertEquals(new BigDecimal("1000.00"), accountA.getBalance().getAmount());
        assertEquals(new BigDecimal("1000.00"), accountB.getBalance().getAmount());
    }

    @Test
    void testIdempotentTransfers() throws InterruptedException {
        String requestId = "unique-request";
        transferService.transfer(requestId, accountA, accountB, new Money(new BigDecimal("100.00"), Currency.DOLLAR));
        transferService.transfer(requestId, accountA, accountB, new Money(new BigDecimal("100.00"), Currency.DOLLAR));

        assertEquals(new BigDecimal("900.00"), accountA.getBalance().getAmount());
        assertEquals(new BigDecimal("1100.00"), accountB.getBalance().getAmount());
    }
}
