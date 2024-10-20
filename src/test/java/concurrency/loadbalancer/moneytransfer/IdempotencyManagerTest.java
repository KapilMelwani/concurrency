package concurrency.loadbalancer.moneytransfer;

import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import org.kapil.concurrency.moneytransfer.IdempotencyManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdempotencyManagerTest {

    @Test
    void testIdempotencyMapClearsAfterTTL() throws InterruptedException {
        IdempotencyManager idempotencyManager = new IdempotencyManager();

        String requestId = "test-request-1";
        idempotencyManager.markAsProcessed(requestId);

        assertTrue(idempotencyManager.isProcessed(requestId));

        TimeUnit.MILLISECONDS.sleep(1100);

        assertFalse(idempotencyManager.isProcessed(requestId));
    }
}
