package com.hsebank.command;

import com.hsebank.domain.Operation;
import com.hsebank.service.OperationService;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class AddOperationCommand implements Command {

    private final OperationService operationService;
    private final Operation.Type type;
    private final String accountId;
    private final String categoryId;
    private final BigDecimal amount;
    private final LocalDate date;
    private final String description;

    public AddOperationCommand(OperationService operationService,
                               Operation.Type type,
                               String accountId,
                               String categoryId,
                               BigDecimal amount,
                               LocalDate date,
                               String description) {
        this.operationService = operationService;
        this.type = type;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    @Override
    public void execute() {
        operationService.create(
                type,
                accountId,
                categoryId,
                amount,
                date,
                description
        );
    }
}
