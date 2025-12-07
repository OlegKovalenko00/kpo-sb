package com.hsebank.command;

import com.hsebank.service.BankAccountService;

public class DeleteAccountCommand implements Command {
    private final BankAccountService service;
    private final String id;

    public DeleteAccountCommand(BankAccountService service, String id) {
        this.service = service;
        this.id = id;
    }

    @Override
    public void execute() {
        try {
            service.delete(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
