package se.nackademin.java20.lab1.domain;

import javax.persistence.*;

@Entity
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "balance")
    private long balance;

    @Column(name = "holder")
    private String holder;

    public Account() {
        //For hibernate
    }

    public Account(String holder, long balance) {
        this.holder = holder;
        this.balance = balance;
    }

    public void withdraw(long amount) {
        long newBalance = balance - amount;
        if (newBalance < 0) throw new IllegalStateException("Balance cannot be less than 0");
        this.balance = this.balance - amount;
    }

    public void deposit(long amount) {
        this.balance = this.balance + amount;
    }

    public long getId() {
        return id;
    }

    public String getHolder() {
        return holder;
    }

    public long getBalance() {
        return balance;
    }
}
