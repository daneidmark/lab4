package se.nackademin.java20.lab1.presentation;

public class AccountDto {
    private final String holder;
    private final long balance;
    private final long id;

    public AccountDto(String holder, long balance, long id) {
        this.holder = holder;
        this.balance = balance;
        this.id = id;
    }

    public String getHolder() {
        return holder;
    }

    public long getBalance() {
        return balance;
    }

    public long getId() {
        return id;
    }
}
