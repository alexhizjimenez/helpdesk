package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.category.application.port.out.CategoryRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<Category> findByDepartmentId(UUID departmentId) {
        return categoryRepository.findByDepartmentId(departmentId).stream().map(categoryPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Category> findByDepartmentIdAndActiveTrue(UUID departmentId) {
        return categoryRepository.findByDepartmentIdAndActiveTrue(departmentId).stream().map(categoryPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Category> findByActiveTrue() {
        return categoryRepository.findByActiveTrue().stream().map(categoryPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return categoryRepository.existsByNameAndIdNot(name,id);
    }

    @Override
    public PageResult<Category> findAllPages(PageQuery pageQuery) {
        Pageable  pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        Page<CategoryEntity> entityPage = categoryRepository.findAll(pageable);
        List<Category> list = entityPage.getContent().stream().map(categoryPersistenceMapper::toDomain).toList();
        return new PageResult<>(list, entityPage.getNumber(), entityPage.getSize(), entityPage.getTotalElements(), entityPage.getTotalPages());
    }
}
