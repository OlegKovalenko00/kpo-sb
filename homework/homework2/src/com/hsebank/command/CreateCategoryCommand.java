package com.hsebank.command;

import com.hsebank.domain.Category;
import com.hsebank.service.CategoryService;

public class CreateCategoryCommand implements Command {

    private final CategoryService categoryService;
    private final Category.Type type;
    private final String name;

    public CreateCategoryCommand(CategoryService categoryService,
                                 Category.Type type,
                                 String name) {
        this.categoryService = categoryService;
        this.type = type;
        this.name = name;
    }

    @Override
    public void execute() {
        categoryService.create(type, name);
    }
}
