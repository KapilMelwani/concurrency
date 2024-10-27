package moneytransfer2;

import java.util.concurrent.atomic.AtomicInteger;

public class Account {
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
}