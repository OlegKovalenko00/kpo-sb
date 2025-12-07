package com.hsebank.export;

import com.hsebank.domain.BankAccount;
import com.hsebank.domain.Category;
import com.hsebank.domain.Operation;

import java.util.Collection;
import java.util.stream.Collectors;

public final class YamlExportVisitor implements ExportVisitor {

    private String safe(Object o) {
        return o == null ? "" : o.toString().replace("\n", " ");
    }

    @Override
    public String exportAccounts(Collection<BankAccount> accounts) {
        return accounts.stream()
                .map(a -> "- id: " + safe(a.getId())
                        + "\n  name: " + safe(a.getName())
                        + "\n  balance: " + safe(a.getBalance()))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String exportCategories(Collection<Category> categories) {
        return categories.stream()
                .map(c -> "- id: " + safe(c.getId())
                        + "\n  type: " + safe(c.getType().name())
                        + "\n  name: " + safe(c.getName()))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String exportOperations(Collection<Operation> operations) {
        return operations.stream()
                .map(o -> "- id: " + safe(o.getId())
                        + "\n  type: " + safe(o.getType().name())
                        + "\n  bankAccountId: " + safe(o.getBankAccountId())
                        + "\n  categoryId: " + safe(o.getCategoryId())
                        + "\n  amount: " + safe(o.getAmount())
                        + "\n  date: " + safe(o.getDate())
                        + "\n  description: " + safe(o.getDescription()))
                .collect(Collectors.joining("\n"));
    }
}
