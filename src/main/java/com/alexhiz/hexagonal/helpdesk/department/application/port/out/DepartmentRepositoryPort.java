package com.alexhiz.hexagonal.helpdesk.department.application.port.out;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepositoryPort {
    Department save(Department department);
    List<Department> findAll();
    Optional<Department> findById(UUID id);
    boolean existsById(UUID id);
    boolean existsByName(String name);
    void delete(UUID id);
}
