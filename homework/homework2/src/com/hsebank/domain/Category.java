package com.hsebank.domain;

import java.util.Objects;
import java.util.UUID;

public final class Category {
    public enum Type { INCOME, EXPENSE }

    private final String id;
    private final Type type;
    private String name;

    public Category(String id, Type type, String name) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.requireNonNull(name); }

    public static Category of(Type type, String name) {
        return new Category(UUID.randomUUID().toString(), type, name);
    }
}
