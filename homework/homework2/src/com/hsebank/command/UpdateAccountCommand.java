package com.hsebank.command;

import com.hsebank.service.BankAccountService;

public class UpdateAccountCommand implements Command {
    private final BankAccountService service;
    private final String id;
    private final String newName;

    public UpdateAccountCommand(BankAccountService service, String id, String newName) {
        this.service = service;
        this.id = id;
        this.newName = newName;
    }

    @Override
    public void execute() {
        try {
            service.updateAccountName(id, newName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
