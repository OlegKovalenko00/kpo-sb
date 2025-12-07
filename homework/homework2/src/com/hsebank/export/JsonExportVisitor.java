package com.hsebank.export;

import com.hsebank.domain.BankAccount;
import com.hsebank.domain.Category;
import com.hsebank.domain.Operation;

import java.util.Collection;

public final class JsonExportVisitor implements ExportVisitor {

    private static String q(String s) {
        if (s == null) return "null";
        String v = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + v + "\"";
    }

    @Override
    public String exportAccounts(Collection<BankAccount> accounts) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (BankAccount a : accounts) {
            if (!first) sb.append(',');
            first = false;
            sb.append('{')
                    .append("\"id\":").append(q(a.getId())).append(',')
                    .append("\"name\":").append(q(a.getName())).append(',')
                    .append("\"balance\":").append(q(a.getBalance().toString()))
                    .append('}');
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public String exportCategories(Collection<Category> categories) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (Category c : categories) {
            if (!first) sb.append(',');
            first = false;
            sb.append('{')
                    .append("\"id\":").append(q(c.getId())).append(',')
                    .append("\"type\":").append(q(c.getType().name())).append(',')
                    .append("\"name\":").append(q(c.getName()))
                    .append('}');
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public String exportOperations(Collection<Operation> operations) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (Operation o : operations) {
            if (!first) sb.append(',');
            first = false;
            sb.append('{')
                    .append("\"id\":").append(q(o.getId())).append(',')
                    .append("\"type\":").append(q(o.getType().name())).append(',')
                    .append("\"bankAccountId\":").append(q(o.getBankAccountId())).append(',')
                    .append("\"categoryId\":").append(q(o.getCategoryId())).append(',')
                    .append("\"amount\":").append(q(o.getAmount().toString())).append(',')
                    .append("\"date\":").append(q(o.getDate().toString())).append(',')
                    .append("\"description\":").append(o.getDescription() == null ? "null" : q(o.getDescription()))
                    .append('}');
        }
        sb.append(']');
        return sb.toString();
    }
}
