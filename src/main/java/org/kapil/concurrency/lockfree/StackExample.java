package org.kapil.concurrency.lockfree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class StackExample {

  public static void main(String[] args) {

   // StandardStack<Integer> stack = new StandardStack<>(); // Standard Stack Count Ops in 10 seconds 205722682
    LockFreeStack<Integer> stack = new LockFreeStack<>(); //Standard Stack Count Ops in 10 seconds 718968516 (x3.5 times faster)
    Random random = new Random();
    for(int i=0;i<100000;i++) {
      stack.push(random.nextInt());
    }
    List<Thread> threadList = new ArrayList<>();

    int pushThread = 2;
    int popThread = 2;

    for(int i=0;i<pushThread;i++) {
      Thread t = new Thread(() -> {
        while(true) {
          stack.push(random.nextInt());
        }
      });
      //t.setDaemon(true);
      threadList.add(t);
    }

    for(int i=0;i<popThread;i++) {
      Thread t = new Thread(() -> {
        while(true) {
          stack.pop();
        }
      });
      //t.setDaemon(true);
      threadList.add(t);
    }

    for(Thread t: threadList) {
      t.start();
    }

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Stack Count Ops in 10 seconds " + stack.getCount());
  }

  public static class LockFreeStack<T> {
    private AtomicReference<StackNode<T>> head = new AtomicReference<>();
    private AtomicInteger count = new AtomicInteger(0);

    public void push(T value) {
      StackNode<T> newHead = new StackNode<>(value);
      while(true) {
        StackNode<T> currentHead = head.get(); // Read current value
        newHead.next = currentHead;  // Calculate new value
        if(head.compareAndSet(currentHead, newHead)) { // Hope value is not changed by another thread
          count.incrementAndGet();
          break;
        } else {
          LockSupport.parkNanos(1); // Repeat the process until succeed
        }
      }
      count.incrementAndGet();
    }

    public T pop() {
      StackNode<T> currentHead = head.get(); // Read current value
      StackNode<T> newHead;
      while(currentHead != null) {
        newHead = currentHead.next; // Calculate new value
        if(head.compareAndSet(currentHead, newHead)) { // Hope value is not changed by another thread
          break;
        } else {
          LockSupport.parkNanos(1); // Repeat the process until succeed
          currentHead = head.get();
        }
      }
      count.incrementAndGet();
      return currentHead != null ? currentHead.value : null;
    }

    public int getCount() {
      return count.get();
    }

  }
  public static class StandardStack<T> {
    private StackNode<T> head;
    private int count = 0;

    public synchronized void push(T value) {
      StackNode<T> newHead = new StackNode<>(value);
      // Race condition - We read head and then we write head. head ref can be changed by another thread
      newHead.next = head;
      head = newHead;
      count++;
    }

    public synchronized T pop() {
      if(head == null) {
        count++; // Race condition - multiple values can change counter
        return null;
      }
      // Race condition - We read the head and the read again and write to it. head ref can be changed by another thread
      T value = head.value;
      head = head.next;
      count++;
      return value;
    }

    public int getCount() {
      return count;
    }

  }

  public static class StackNode<T> {
    private T value;
    private StackNode<T> next;

    public StackNode(T value) {
      this.value = value;
    }
  }
}
