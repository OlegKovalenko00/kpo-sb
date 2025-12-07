package com.hsebank.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Operation {
    public enum Type { INCOME, EXPENSE }

    private final String id;
    private final Type type;
    private final String bankAccountId;
    private final String categoryId;
    private final BigDecimal amount;
    private final LocalDate date;
    private final String description;

    public Operation(String id,
                     Type type,
                     String bankAccountId,
                     String categoryId,
                     BigDecimal amount,
                     LocalDate date,
                     String description) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.bankAccountId = Objects.requireNonNull(bankAccountId);
        this.categoryId = Objects.requireNonNull(categoryId);
        this.amount = Objects.requireNonNull(amount);
        this.date = Objects.requireNonNull(date);
        this.description = description;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getBankAccountId() { return bankAccountId; }
    public String getCategoryId() { return categoryId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }

    public static Operation of(Type type,
                               String bankAccountId,
                               String categoryId,
                               BigDecimal amount,
                               LocalDate date,
                               String description) {
        return new Operation(UUID.randomUUID().toString(), type, bankAccountId, categoryId, amount, date, description);
    }
}
