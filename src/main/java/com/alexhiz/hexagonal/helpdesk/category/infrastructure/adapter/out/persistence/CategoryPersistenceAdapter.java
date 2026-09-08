package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.category.application.port.out.CategoryRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {
    private final CategoryRepository categoryRepository;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = categoryRepository.save(categoryPersistenceMapper.toEntity(category));
        return categoryPersistenceMapper.toDomain(entity);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id).map(categoryPersistenceMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll().stream().map(categoryPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }
}
