package org.kapil.concurrency.locks.reenterantlock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueProducerConsumer {

  public static void main(String[] args) {
    Buffer buffer = new Buffer(1);

    for (int i = 0; i < 1; i++) {
      new Thread(new Producer(buffer)).start();
      new Thread(new Consumer(buffer)).start();
    }
  }

  static class Buffer {

    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public Buffer(int capacity) {
      this.capacity = capacity;
    }

    public void produce(int value) throws InterruptedException {
      lock.lock();
      try {
        while (queue.size() == capacity) {
          notFull.await(); // Espera a que haya espacio disponible
        }
        queue.add(value);
        System.out.println("Produced: " + value);
        notEmpty.signal(); // Señala que hay ítems disponibles para consumir
      } finally {
        lock.unlock();
      }
    }

    public int consume() throws InterruptedException {
      lock.lock();
      try {
        while (queue.isEmpty()) {
          notEmpty.await(); // Espera a que haya ítems disponibles
        }
        int value = queue.poll();
        System.out.println("Consumed: " + value);
        notFull.signal(); // Señala que hay espacio disponible para producir
        return value;
      } finally {
        lock.unlock();
      }
    }
  }

  static class Producer implements Runnable {

    private final Buffer buffer;

    public Producer(Buffer buffer) {
      this.buffer = buffer;
    }

    @Override
    public void run() {
      int value = 0;
      while (true) {
        try {
          buffer.produce(value++);
          Thread.sleep(1000); // Simula el tiempo de producción
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  static class Consumer implements Runnable {

    private final Buffer buffer;

    public Consumer(Buffer buffer) {
      this.buffer = buffer;
    }

    @Override
    public void run() {
      while (true) {
        try {
          buffer.consume();
          Thread.sleep(1000); // Simula el tiempo de consumo
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}

