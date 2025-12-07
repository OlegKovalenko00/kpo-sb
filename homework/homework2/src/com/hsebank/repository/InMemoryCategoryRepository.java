package com.hsebank.repository;


import com.hsebank.domain.Category;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public final class InMemoryCategoryRepository implements CategoryRepository {
    private final Map<String, Category> map = new ConcurrentHashMap<>();


    public Category save(Category category) { map.put(category.getId(), category); return category; }
    public Optional<Category> findById(String id) { return Optional.ofNullable(map.get(id)); }
    public Collection<Category> findAll() { return Collections.unmodifiableCollection(map.values()); }
    public void delete(String id) { map.remove(id); }
}