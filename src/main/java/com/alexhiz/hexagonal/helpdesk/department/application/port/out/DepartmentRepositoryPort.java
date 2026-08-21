package com.alexhiz.hexagonal.helpdesk.department.application.port.out;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;

import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepositoryPort {
    Department save(Department department);
    Optional<Department> findById(UUID id);
    boolean existsById(UUID id);
    boolean existsByName(String name);
}
