package Guiao7.Exe1e2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Bank {
    private Map<Integer, Account> accounts = new HashMap<Integer, Account>();
    private int nextAccount;
    private int wew;
    ReentrantLock lock = new ReentrantLock();

    public Bank(int numAccounts) {
        this.nextAccount = numAccounts;
        for (int i = 0; i < numAccounts; i++) {
            this.accounts.put(i, new Account(0));
        }
    }

    public int createAccount(float initBalance) {
        int ret = this.nextAccount;
        this.nextAccount++;
        Account newAccount = new Account(initBalance);
        lock.lock();
        try {
            this.accounts.put(ret, newAccount);
            System.out.println("[" + Thread.currentThread().getName() + "] Acc " + ret + " created with balance:" + initBalance);
        } finally {
            lock.unlock();
        }

        return ret;
    }

    public float closeAccount(int id) throws InvalidAccount {
        Account a;
        validId(id);
        lock.lock();
        try {
            a = this.accounts.get(id);
            this.accounts.remove(id);
            System.out.println("[" + Thread.currentThread().getName() + "] Acc " + id + " removed.");

        } finally {
            lock.unlock();
        }
        return 1;
    }

    public void withdraw(int id, float amount) throws NotEnoughFunds, InvalidAccount {
        lock.lock();
        try {
            validId(id);
            validFunds(id, amount);
            this.accounts.get(id).lock.lock();
        } finally {
            lock.unlock();
        }
        try {
            this.accounts.get(id).withdraw(amount);
            this.accounts.get(id).addMov(new Movements(+1, "Withdraw", amount, this.accounts.get(id).getBalance()));
        } finally {
            this.accounts.get(id).lock.unlock();
        }
    }

    public void deposit(int id, float amount) throws InvalidAccount {
        lock.lock();
        try {
            validId(id);
            this.accounts.get(id).lock.lock();
        } finally {
            lock.unlock();
        }
        try {
            this.accounts.get(id).deposit(amount);
            this.accounts.get(id).addMov(new Movements(+1, "Deposit", amount, this.accounts.get(id).getBalance()));


        } finally {
            this.accounts.get(id).lock.unlock();
        }
    }

    public void transfer(int from, int to, float amount) throws InvalidAccount, InterruptedException, NotEnoughFunds {
        validId(from);
        validId(to);
        acquireLocks(this.accounts.get(from).lock, this.accounts.get(to).lock);
        try {
            this.accounts.get(from).withdraw(amount);
            this.accounts.get(to).deposit(amount);
            this.accounts.get(from).addMov(new Movements(+1, "Transfer to " + to, amount, this.accounts.get(from).getBalance()));
            this.accounts.get(to).addMov(new Movements(+1, "Transfer from " + from, amount, this.accounts.get(to).getBalance()));


        } finally {
            this.accounts.get(from).lock.unlock();
            this.accounts.get(to).lock.unlock();
        }

    }

    public float balance(int id) throws InvalidAccount {
        float accBalance;
        lock.lock();
        try {
            validId(id);
            this.accounts.get(id).lock.lock();
        } finally {
            lock.unlock();
        }
        try {
            accBalance = this.accounts.get(id).getBalance();
        } finally {
            this.accounts.get(id).lock.unlock();
        }
        return accBalance;
    }

    public float totalBalance(int[] accs) throws InvalidAccount {
        float totalBalance = 0;
        lock.lock();
        try {
            for (int i = 0; i < accs.length; i++) {
                this.accounts.get(accs[i]).lock.lock();
            }
        } finally {
            lock.unlock();
        }
        for (int i = 0; i < accs.length; i++) {
            totalBalance += this.accounts.get(accs[i]).getBalance();
            this.accounts.get(accs[i]).lock.unlock();
        }
        return totalBalance;

    }

    public String getMovsFromAccount(int id) throws InvalidAccount {
        validId(id);
        lock.lock();
        try {
            this.accounts.get(id).lock.lock();
        } finally {
            lock.unlock();
        }
        try {
            return this.accounts.get(id).movToString();
        } finally {
            this.accounts.get(id).lock.unlock();
        }
    }


    public int[] accsToArray() {
        int[] accs = new int[this.accounts.size()];
        for (int i = 0; i < this.accounts.size(); i++) {
            accs[i] = i;
        }

        return accs;
    }

    public void printAccounts() {
        this.accounts.forEach((k, v) -> System.out.println("Account: " + k + " Balance: " + v.getBalance()));
    }

    public void validId(int id) throws InvalidAccount {
        if ( id < 0 || id >= accounts.size() ) {
            throw new InvalidAccount(id);
        }
    }

    public void validFunds(int id, float val) throws NotEnoughFunds {
        if ( accounts.get(id).getBalance() < val ) {
            throw new NotEnoughFunds(id);
        }
    }

    public void acquireLocks(Lock firstLock, Lock secondLock) throws InterruptedException {
        while (true) {

            boolean gotFirstLock = false;
            boolean gotSecondLock = false;

            try {
                gotFirstLock = firstLock.tryLock();
                gotSecondLock = secondLock.tryLock();
            } finally {
                if ( gotFirstLock && gotSecondLock ) {
                    return;
                }

                if ( gotFirstLock ) {
                    firstLock.unlock();
                }
                if ( gotSecondLock ) {
                    secondLock.unlock();
                }
            }
            Thread.sleep(1);
        }
    }


}

class InvalidAccount extends Exception {
    int id;

    InvalidAccount(int id) {
        this.id = id;
    }

}

class NotEnoughFunds extends Exception {
    int id;

    NotEnoughFunds(int id) {
        this.id = id;
    }

    NotEnoughFunds() {

    }
}