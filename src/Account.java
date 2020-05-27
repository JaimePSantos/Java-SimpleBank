package Guiao7.Exe1e2;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private float balance = 0;
    ReentrantLock lock = new ReentrantLock();
    private List<Movements> movs;

    public Account(float initBalance) {
        this.balance = initBalance;
        this.movs = new ArrayList<>();
    }

    public void deposit(float amount) {
        this.balance += amount;

    }

    public void withdraw(float amount) throws NotEnoughFunds {
        if ( amount > this.balance ) {
            throw new NotEnoughFunds();
        } else {
            this.balance -= amount;
        }


    }

    public float getBalance() {
        return this.balance;
    }

    public void addMov(Movements mov) {
        this.movs.add(mov);
    }

    public List<Movements> getMovs() {
        return new ArrayList<>(this.movs);
    }

    public String movToString() {
        String movementString = "";
        for (Movements mov : this.movs) {
            movementString += mov.toString() + "|\t\t";
        }
        return movementString;
    }

}