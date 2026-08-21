package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, UUID> {
    boolean existsByName(String name);
    Optional<DepartmentEntity> findByName(String name);
}
