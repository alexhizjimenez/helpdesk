package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.category.application.port.out.CategoryRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.category.domain.exception.CategoryNotFoundException;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
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
        return categoryRepository.findByPublicId(id).map(categoryPersistenceMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll().stream().map(categoryPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return categoryRepository.existsByPublicId(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public void delete(UUID id) {
        CategoryEntity category = categoryRepository.findByPublicId(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.setActive(false);
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.saveAndFlush(category);
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
        return categoryRepository.existsByNameAndPublicIdNot(name,id);
    }

    @Override
    public PageResult<Category> findAllPages(PageQuery pageQuery) {
        Pageable  pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        Page<CategoryEntity> entityPage = categoryRepository.findAll(pageable);
        List<Category> list = entityPage.getContent().stream().map(categoryPersistenceMapper::toDomain).toList();
        return new PageResult<>(list, entityPage.getNumber(), entityPage.getSize(), entityPage.getTotalElements(), entityPage.getTotalPages());
    }

    @Override
    public Category restoreCategory(UUID id) {
        CategoryEntity category = categoryRepository.findByPublicId(id).orElseThrow(() -> new CategoryNotFoundException(id));
        log.info("Before restore - deletedAt: {}, updatedAt: {}",
                category.getDeletedAt(), category.getUpdatedAt());

        if (category.getDeletedAt() == null) {
            log.info("Category already restored: {}", id);
            return categoryPersistenceMapper.toDomain(category);
        }
        category.setActive(true);
        category.setDeletedAt(null);
        CategoryEntity saved = categoryRepository.saveAndFlush(category);

        log.info("After restore - deletedAt: {}, updatedAt: {}",
                saved.getDeletedAt(), saved.getUpdatedAt());
        return categoryPersistenceMapper.toDomain(saved);
    }
}
