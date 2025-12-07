package com.hsebank.repository;


import com.hsebank.domain.Category;


import java.util.Collection;
import java.util.Optional;


public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(String id);
    Collection<Category> findAll();
    void delete(String id);
}