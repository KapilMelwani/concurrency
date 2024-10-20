package org.kapil.concurrency.lockfree;

import java.util.concurrent.atomic.AtomicReference;
public class AtomicReferenceExample {

  public static void main(String[] args) {
    String oldValue = "old value";
    String newValue = "new value";
    AtomicReference<String> atomicReference = new AtomicReference<>(oldValue);
    atomicReference.set("Unexpected value");
    if(atomicReference.compareAndSet(oldValue, newValue)) {
      System.out.println("New value is " + atomicReference.get());
    } else {
      System.out.println("Value was not updated");
    }
  }
}
