package se.nackademin.java20.lab1.presentation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionRequest {
    private final long accountId;
    private final int amount;

    @JsonCreator
    public TransactionRequest(@JsonProperty("accountId") long accountId, @JsonProperty("amount") int amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public long getAccountId() {
        return accountId;
    }
}
