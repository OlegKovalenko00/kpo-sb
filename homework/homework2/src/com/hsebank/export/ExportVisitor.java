package com.hsebank.export;


import com.hsebank.domain.BankAccount;
import com.hsebank.domain.Category;
import com.hsebank.domain.Operation;


import java.util.Collection;


public interface ExportVisitor {
    String exportAccounts(Collection<BankAccount> accounts);
    String exportCategories(Collection<Category> categories);
    String exportOperations(Collection<Operation> operations);
}