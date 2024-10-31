package org.kapil.concurrency.moneytransfer2;

import java.util.concurrent.atomic.AtomicInteger;

public class MoneyTransferService {

    private static class Account {
        private AtomicInteger balance;

        public Account(int initialBalance) {
            balance = new AtomicInteger(initialBalance);
        }

        public boolean transfer(Account toAccount, int amount) {
            int currentBalance = balance.get();

            if (currentBalance < amount) {
                return false;
            }

            boolean successfulWithdrawal = balance.compareAndSet(currentBalance, currentBalance - amount);

            if (successfulWithdrawal) {
                toAccount.deposit(amount);
            }

            return successfulWithdrawal;
        }

        public void deposit(int amount) {
            balance.getAndAdd(amount);
        }

        public int getBalance() {
            return balance.get();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account accountA = new Account(1000);
        Account accountB = new Account(1000);

        int numThreads = 10;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            final int amount = 100;
            threads[i] = new Thread(() -> {
                accountA.transfer(accountB, amount);
            });
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }

        System.out.println("Account A balance: " + accountA.getBalance());
        System.out.println("Account B balance: " + accountB.getBalance());
    }
}