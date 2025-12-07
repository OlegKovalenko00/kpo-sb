package com.hsebank.command;

import com.hsebank.service.OperationService;

public class DeleteOperationCommand implements Command {
    private final OperationService service;
    private final String id;

    public DeleteOperationCommand(OperationService service, String id) {
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
