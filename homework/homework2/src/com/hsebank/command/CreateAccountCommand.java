package com.hsebank.command;

import com.hsebank.service.BankAccountService;

import java.math.BigDecimal;

public final class CreateAccountCommand implements Command {

    private final BankAccountService service;
    private final String name;
    private final BigDecimal initialBalance;

    public CreateAccountCommand(BankAccountService service,
                                String name,
                                BigDecimal initialBalance) {
        this.service = service;
        this.name = name;
        this.initialBalance = initialBalance;
    }

    public CreateAccountCommand(BankAccountService service,
                                String name) {
        this(service, name, BigDecimal.ZERO);
    }

    @Override
    public void execute() {
        service.create(name, initialBalance);
    }
}
