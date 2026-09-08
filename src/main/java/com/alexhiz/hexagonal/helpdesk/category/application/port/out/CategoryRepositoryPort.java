package com.alexhiz.hexagonal.helpdesk.category.application.port.out;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    List<Category> findAll();
    boolean existsById(UUID id);
    boolean existsByName(String name);
    void delete(UUID id);
}
