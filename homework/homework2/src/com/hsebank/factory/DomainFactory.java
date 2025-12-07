package com.hsebank.factory;

import com.hsebank.domain.BankAccount;
import com.hsebank.domain.Category;
import com.hsebank.domain.Operation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class DomainFactory {

    public BankAccount createBankAccount(String name) {
        return new BankAccount(UUID.randomUUID().toString(), name, BigDecimal.ZERO);
    }

    public BankAccount createBankAccount(String name, BigDecimal balance) {
        BigDecimal bal = balance == null ? BigDecimal.ZERO : balance;
        return new BankAccount(UUID.randomUUID().toString(), name, bal);
    }

    public BankAccount createBankAccount(String id, String name, BigDecimal balance) {
        String realId = id == null ? UUID.randomUUID().toString() : id;
        BigDecimal bal = balance == null ? BigDecimal.ZERO : balance;
        return new BankAccount(realId, name, bal);
    }

    public Category createCategory(Category.Type type, String name) {
        return new Category(UUID.randomUUID().toString(), type, name);
    }

    public Category createCategory(String id, Category.Type type, String name) {
        String realId = id == null ? UUID.randomUUID().toString() : id;
        return new Category(realId, type, name);
    }

    // --- Operation ---

    public Operation createOperation(Operation.Type type,
                                     String bankAccountId,
                                     String categoryId,
                                     BigDecimal amount,
                                     LocalDate date,
                                     String description) {
        validateAmount(amount);
        return new Operation(UUID.randomUUID().toString(), type, bankAccountId, categoryId, amount, date, description);
    }

    public Operation createOperation(String id,
                                     Operation.Type type,
                                     String bankAccountId,
                                     String categoryId,
                                     BigDecimal amount,
                                     LocalDate date,
                                     String description) {
        validateAmount(amount);
        String realId = id == null ? UUID.randomUUID().toString() : id;
        return new Operation(realId, type, bankAccountId, categoryId, amount, date, description);
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount is required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
