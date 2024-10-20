package concurrency.loadbalancer.moneytransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.kapil.concurrency.moneytransfer.BankAccount;
import org.kapil.concurrency.moneytransfer.IdempotencyManager;
import org.kapil.concurrency.moneytransfer.MoneyTransferService;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTransferServiceTest {

    private BankAccount accountA;
    private BankAccount accountB;
    private MoneyTransferService transferService;
    private IdempotencyManager idempotencyManager;

    @BeforeEach
    void setup() {
        accountA = new BankAccount("A", new BigDecimal("1000.00"));
        accountB = new BankAccount("B", new BigDecimal("1000.00"));
        idempotencyManager = new IdempotencyManager();
        transferService = new MoneyTransferService(idempotencyManager);
    }

    @Test
    void testConcurrentWithdrawals() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final String requestId = "withdraw-request-" + i;
            executor.submit(() -> {
                transferService.transfer(requestId, accountA, accountB, new BigDecimal("100.00"));
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(new BigDecimal("0.00"), accountA.getBalance());
        assertEquals(new BigDecimal("2000.00"), accountB.getBalance());
    }

    @Test
    void testConcurrentDeposits() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final String requestId = "deposit-request-" + i;
            executor.submit(() -> {
                transferService.transfer(requestId, accountB, accountA, new BigDecimal("100.00"));
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(new BigDecimal("2000.00"), accountA.getBalance());
        assertEquals(new BigDecimal("0.00"), accountB.getBalance());
    }

    @Test
    void testMixedConcurrentTransfers() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final String requestId = "transfer-request-" + i;
            if (i % 2 == 0) {
                executor.submit(() -> transferService.transfer(requestId, accountA, accountB, new BigDecimal("50.00")));
            } else {
                executor.submit(() -> transferService.transfer(requestId, accountB, accountA, new BigDecimal("50.00")));
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        int transfersToB = numThreads / 2;
        int transfersToA = numThreads / 2;

        BigDecimal expectedBalanceA = new BigDecimal("1000.00").add(new BigDecimal(transfersToA * 50)).subtract(new BigDecimal(transfersToB * 50));
        BigDecimal expectedBalanceB = new BigDecimal("1000.00").add(new BigDecimal(transfersToB * 50)).subtract(new BigDecimal(transfersToA * 50));

        assertEquals(expectedBalanceA, accountA.getBalance());
        assertEquals(expectedBalanceB, accountB.getBalance());
    }


    @Test
    void testIdempotentTransfers() throws InterruptedException {
        String requestId = "unique-request";
        transferService.transfer(requestId, accountA, accountB, new BigDecimal("100.00"));
        transferService.transfer(requestId, accountA, accountB, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("900.00"), accountA.getBalance());
        assertEquals(new BigDecimal("1100.00"), accountB.getBalance());
    }
}
