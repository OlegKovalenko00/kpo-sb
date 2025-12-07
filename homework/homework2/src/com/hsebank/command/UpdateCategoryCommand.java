package com.hsebank.command;

import com.hsebank.service.CategoryService;
import com.hsebank.domain.Category;

public class UpdateCategoryCommand implements Command {
    private final CategoryService service;
    private final String id;
    private final Category.Type newType;
    private final String newName;

    public UpdateCategoryCommand(CategoryService service, String id, Category.Type newType, String newName) {
        this.service = service;
        this.id = id;
        this.newType = newType;
        this.newName = newName;
    }

    @Override
    public void execute() {
        try {
            service.updateCategory(id, newType, newName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
