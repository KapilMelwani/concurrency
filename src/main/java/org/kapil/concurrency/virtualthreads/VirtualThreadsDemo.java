package org.kapil.concurrency.virtualthreads;

import java.util.ArrayList;
import java.util.List;

public class VirtualThreadsDemo{

  private static final int N_VIRTUAL_THREADS = 10;

  public static void main(String[] args) throws InterruptedException {
    // 1. Inside thread: Thread[#21,Thread-0,5,main], thread0, 5 priority (Default) which parent is main
    // 2. Same as 1 but other method of creating a platform thread
    /* 3. VT creation: Inside thread: VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
    * Virtual Thread with ID 21 name runnable@ForkJoinPool-1-worker-1 which means that:
    * 1. JVM created an internal thread pool of platform threads called ForkJoinPool-1
    * 2. JVM mounter our VT in one of those worker threads called worker 1.
    *
    * 4. When running for 20 VT, we can see that the JVM decided to dynamically create a pool of
    * 5 platform threads to be their carriers inside the pool of threads ForkJoinPool-1
    *
    * Inside thread: VirtualThread[#36]/runnable@ForkJoinPool-1-worker-3
      Inside thread: VirtualThread[#37]/runnable@ForkJoinPool-1-worker-4
      Inside thread: VirtualThread[#38]/runnable@ForkJoinPool-1-worker-4
      Inside thread: VirtualThread[#39]/runnable@ForkJoinPool-1-worker-3
      Inside thread: VirtualThread[#40]/runnable@ForkJoinPool-1-worker-4
      Inside thread: VirtualThread[#25]/runnable@ForkJoinPool-1-worker-5
      * ....
      AS DEVELOPERS WE CANNOT CONTROL HOW THE JVM ASSIGNS THE PT TO THE VT
    */

    //Runnable runnable  = () -> System.out.println("Inside thread: " + Thread.currentThread());
    // Thread platformThread = new Thread(runnable);
    // Thread platformThread = Thread.ofPlatform().unstarted(runnable);
    //platformThread.start();
    //platformThread.join();

    List<Thread> virtualThreads = new ArrayList<>();
    for (int i=0;i<N_VIRTUAL_THREADS;i++) {
      Thread virtualThread = Thread.ofVirtual().unstarted(new BlockingTask());
      virtualThreads.add(virtualThread);
    }

    for(Thread vt : virtualThreads) {
      vt.start();
    }

    for(Thread vt : virtualThreads) {
      vt.join();
    }
  }

  private static class BlockingTask implements Runnable {

    @Override
    public void run() {
      System.out.println("Inside thread: " + Thread.currentThread() + " before call");
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      System.out.println("Inside thread: " + Thread.currentThread() + " after call");
    }
  }
}
