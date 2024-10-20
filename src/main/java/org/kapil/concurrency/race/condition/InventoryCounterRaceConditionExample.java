package org.kapil.concurrency.race.condition;

/**
 *  This is a shared objects between both threads. Both threads are accessing the same object.
 *  Incrementing and decrementing the items in the inventory (those ops are not ATOMIC).
 */
public class InventoryCounterRaceConditionExample {
    public static void main(String[] args) throws InterruptedException {

        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("We currently have " + inventoryCounter.getItems() + " items");
    }

    public static class DecrementingThread extends Thread {

        private InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    public static class IncrementingThread extends Thread {

        private InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.increment();
            }
        }
    }

   /* private static class InventoryCounterExample {
        private final AtomicInteger items = new AtomicInteger(0);

        public void increment() {
            items.getAndIncrement();
        }

        public void decrement() {
            items.getAndDecrement();
        }

        public int getItems() {
            return items.get();
        }
    }*/

    /**
     * synchronized block is a locking mechanism used to restrict access to a critical section or
     * entire method to a single thread at a time.
     * */
    private static class InventoryCounter {
        private int items = 0;

        final Object lock = new Object();

        public void increment() {
            synchronized (this.lock) {
                items++;
            }
        }

        public void decrement() {
            synchronized (this.lock) {
                items--;
            }
        }

        public int getItems() {
            return items;
        }
    }
}
