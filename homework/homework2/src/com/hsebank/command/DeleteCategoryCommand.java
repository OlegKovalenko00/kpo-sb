package com.hsebank.command;

import com.hsebank.service.CategoryService;

public class DeleteCategoryCommand implements Command {
    private final CategoryService service;
    private final String id;

    public DeleteCategoryCommand(CategoryService service, String id) {
        this.service = service;
        this.id = id;
    }

    @Override
    public void execute() {
        service.delete(id);
    }
}
