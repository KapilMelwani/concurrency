package org.kapil.concurrency.example2;

public class SingleThreadCreation {

  public static void main(String[] args) throws InterruptedException {
    Thread thread = new NewThread();
    thread.start();
  }

  private static class NewThread extends Thread {
    @Override
    public void run() {
      System.out.println("We are in thread: " + this.getName());
    }
  }
}