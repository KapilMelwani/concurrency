package org.kapil.concurrency.leetcode;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

class H2O {

    /*
     Se inicializa con un valor de 3, lo que significa que esperará a que tres hilos llamen a await().
     Este valor coincide con la cantidad de átomos necesarios para formar una molécula de agua: 2 hidrógenos y 1 oxígeno
     */
    private final CyclicBarrier barrier = new CyclicBarrier(3);
    private final Semaphore hSem = new Semaphore(2);
    private final Semaphore oSem = new Semaphore(1);

    public H2O() {
    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        try {
            hSem.acquire();
            barrier.await();
            releaseHydrogen.run();
        } catch(Exception ignore) {

        } finally {
            hSem.release();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        try {
            oSem.acquire();
            barrier.await();
            releaseOxygen.run();
        } catch(Exception ignore) {

        } finally {
            oSem.release();
        }
    }

    public static void main(String[] args) {
        String input = "OOHHHH";
        H2O h2o = new H2O();
        for (char c : input.toCharArray()) {
            if (c == 'H') {
                new Thread(() -> {
                    try {
                        h2o.hydrogen(() -> System.out.print("H"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                new Thread(() -> {
                    try {
                        h2o.oxygen(() -> System.out.print("O"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}