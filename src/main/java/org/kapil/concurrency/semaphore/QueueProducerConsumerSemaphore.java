package org.kapil.concurrency.semaphore;

import java.util.concurrent.Semaphore;

public class QueueProducerConsumerSemaphore {

  public static void main(String[] args) {
    Semaphore items = new Semaphore(0); // Controls the number of produced items
    Semaphore spaces = new Semaphore(10); // Controls the available spaces (buffer capacity)

    // Start producer threads
    for (int i = 0; i < 100; i++) { // 3 producers
      Thread t = new Thread(new Producer(spaces, items));
      t.setDaemon(true);
    }

    // Start consumer threads
    for (int i = 0; i < 100; i++) { // 3 consumers
      Thread t =  new Thread(new Consumer(spaces, items));
      t.setDaemon(true);
    }
  }

  // Producer class
  static class Producer implements Runnable {
    private final Semaphore spaces;
    private final Semaphore items;

    public Producer(Semaphore spaces, Semaphore items) {
      this.spaces = spaces;
      this.items = items;
    }

    @Override
    public void run() {
      while (true) {
        try {
          spaces.acquire(); // Waits for available space in the buffer
          produce();
          items.release(); // Increases the number of produced items
        } catch (InterruptedException e) {
          System.out.println("Producer thread was interrupted.");
          e.printStackTrace();
        }
      }
    }

    private void produce() throws InterruptedException {
      System.out.println(Thread.currentThread().getName() + " produced an item.");
      Thread.sleep(1000); // Simulates production time
    }
  }

  // Consumer class
  static class Consumer implements Runnable {
    private final Semaphore spaces;
    private final Semaphore items;

    public Consumer(Semaphore spaces, Semaphore items) {
      this.spaces = spaces;
      this.items = items;
    }

    @Override
    public void run() {
      while (true) {
        try {
          items.acquire(); // Waits for available items to consume
          consume();
          spaces.release(); // Increases the number of available spaces
        } catch (InterruptedException e) {
          System.out.println("Consumer thread was interrupted.");
          e.printStackTrace();
        }
      }
    }

    private void consume() throws InterruptedException {
      System.out.println(Thread.currentThread().getName() + " consumed an item.");
      Thread.sleep(1000); // Simulates consumption time
    }
  }
}
