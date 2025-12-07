package com.hsebank.service;

import com.hsebank.domain.Category;
import com.hsebank.factory.DomainFactory;
import com.hsebank.repository.CategoryRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;

public final class CategoryService {
    private final CategoryRepository repo;
    private final DomainFactory factory;

    public CategoryService(CategoryRepository repo, DomainFactory factory) {
        this.repo = repo;
        this.factory = factory;
    }

    public Category create(Category.Type type, String name) {
        Category c = factory.createCategory(type, name);
        return repo.save(c);
    }

    public Category createOrUpdate(String id, Category.Type type, String name) {
        if (id != null) {
            Optional<Category> existing = repo.findById(id);
            if (existing.isPresent()) {
                Category c = existing.get();
                boolean updated = trySetters(c, type, name);
                if (updated) return repo.save(c);
                // fall through to replace if no setters
            }
        }
        Category c = factory.createCategory(type, name);
        if (id != null) {
            try {
                Field idField = c.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(c, id);
            } catch (Exception e) {
                System.out.println("warning: cannot set id on created Category: " + e.getMessage());
            }
        }
        return repo.save(c);
    }

    public void updateCategory(String id, Category.Type type, String name) {
        Category c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("category not found"));
        boolean updated = trySetters(c, type, name);
        if (updated) {
            repo.save(c);
            return;
        }
        // если сеттеров нет — заменяем объект
        Category newC = factory.createCategory(type, name);
        try {
            Field idField = newC.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(newC, id);
        } catch (Exception e) {
            System.out.println("warning: cannot preserve id when updating category: " + e.getMessage());
        }
        repo.delete(id);
        repo.save(newC);
    }

    private boolean trySetters(Category c, Category.Type type, String name) {
        boolean changed = false;
        try {
            Method mType = c.getClass().getMethod("setType", Category.Type.class);
            mType.invoke(c, type);
            changed = true;
        } catch (NoSuchMethodException ignored) { }
        catch (Exception e) { System.out.println("warning: failed to invoke setType: " + e.getMessage()); }

        try {
            Method mName = c.getClass().getMethod("setName", String.class);
            mName.invoke(c, name);
            changed = true;
        } catch (NoSuchMethodException ignored) { }
        catch (Exception e) { System.out.println("warning: failed to invoke setName: " + e.getMessage()); }

        return changed;
    }

    public Optional<Category> find(String id) { return repo.findById(id); }
    public Collection<Category> list() { return repo.findAll(); }
    public void delete(String id) { repo.delete(id); }
}
