package com.hsebank.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class BankAccount {
    private final String id;
    private String name;
    private BigDecimal balance;

    public BankAccount(String id, String name, BigDecimal balance) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.balance = Objects.requireNonNull(balance);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = Objects.requireNonNull(balance);
    }

    public void applyDelta(BigDecimal delta) {
        this.balance = this.balance.add(Objects.requireNonNull(delta));
    }

    public static BankAccount createDefault(String name) {
        return new BankAccount(UUID.randomUUID().toString(), name, BigDecimal.ZERO);
    }
}
