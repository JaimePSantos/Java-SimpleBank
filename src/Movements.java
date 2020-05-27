package Guiao7.Exe1e2;

public class Movements {
    private int opId;
    private String movement;
    private double amount;
    private double movBalance;

    public Movements(int opId, String movement, double amount, double movBalance) {
        this.opId = opId;
        this.movement = movement;
        this.amount = amount;
        this.movBalance = movBalance;
    }

    public String toString() {
        return opId + "> " + movement + "\tAmount: " + amount + "\t Balance: " + movBalance;
    }
}
