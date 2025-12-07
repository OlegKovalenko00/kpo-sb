package com.hsebank.command;

import com.hsebank.service.OperationService;
import com.hsebank.domain.Operation;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateOperationCommand implements Command {
    private final OperationService service;
    private final String id;
    private final Operation.Type type;
    private final String accountId;
    private final String categoryId;
    private final BigDecimal amount;
    private final LocalDate date;
    private final String description;

    public UpdateOperationCommand(OperationService service, String id, Operation.Type type, String accountId, String categoryId, BigDecimal amount, LocalDate date, String description) {
        this.service = service;
        this.id = id;
        this.type = type;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    @Override
    public void execute() {
        try {
            service.updateOperation(id, type, accountId, categoryId, amount, date, description);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
