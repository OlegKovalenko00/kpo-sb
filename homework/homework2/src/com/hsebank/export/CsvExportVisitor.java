package com.hsebank.export;

import com.hsebank.domain.BankAccount;
import com.hsebank.domain.Category;
import com.hsebank.domain.Operation;

import java.util.Collection;

public final class CsvExportVisitor implements ExportVisitor {

    @Override
    public String exportAccounts(Collection<BankAccount> accounts) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,name,balance").append('\n');
        boolean first = true;
        for (BankAccount a : accounts) {
            if (!first) sb.append('\n');
            first = false;
            sb.append(a.getId()).append(',')
                    .append(escape(a.getName())).append(',')
                    .append(a.getBalance());
        }
        return sb.toString();
    }

    @Override
    public String exportCategories(Collection<Category> categories) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name").append('\n');
        boolean first = true;
        for (Category c : categories) {
            if (!first) sb.append('\n');
            first = false;
            sb.append(c.getId()).append(',')
                    .append(escape(c.getType().name())).append(',')
                    .append(escape(c.getName()));
        }
        return sb.toString();
    }

    @Override
    public String exportOperations(Collection<Operation> operations) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,accountId,categoryId,amount,date,description").append('\n');
        boolean first = true;
        for (Operation o : operations) {
            if (!first) sb.append('\n');
            first = false;
            sb.append(o.getId()).append(',')
                    .append(escape(o.getType().name())).append(',')
                    .append(escape(o.getBankAccountId())).append(',')
                    .append(escape(o.getCategoryId())).append(',')
                    .append(o.getAmount()).append(',')
                    .append(o.getDate()).append(',')
                    .append(escape(o.getDescription()));
        }
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n")) {
            return "\"" + v + "\"";
        }
        return v;
    }
}
