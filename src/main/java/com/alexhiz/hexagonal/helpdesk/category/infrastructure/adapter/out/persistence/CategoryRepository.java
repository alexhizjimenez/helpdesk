package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByName(String name);
    List<CategoryEntity> findByDepartmentId(UUID departmentId);
    List<CategoryEntity> findByDepartmentIdAndActiveTrue(UUID departmentId);
    List<CategoryEntity> findByActiveTrue();
    boolean existsByNameAndPublicIdNot(String name, UUID id);
    Optional<CategoryEntity> findByPublicId(UUID id);
    boolean existsByPublicId(UUID id);


    @Override
    @EntityGraph(attributePaths = {"department"})
    List<CategoryEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"department"})
    Page<CategoryEntity> findAll(Pageable pageable);
}
