package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByName(String name);
    List<CategoryEntity> findByDepartmentId(UUID departmentId);
    List<CategoryEntity> findByDepartmentIdAndActiveTrue(UUID departmentId);
    List<CategoryEntity> findByActiveTrue();
    boolean existsByNameAndIdNot(String name, UUID id);
}
