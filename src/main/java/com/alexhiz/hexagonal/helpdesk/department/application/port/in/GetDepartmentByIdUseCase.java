package com.alexhiz.hexagonal.helpdesk.department.application.port.in;

import java.util.Optional;
import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;

public interface GetDepartmentByIdUseCase {
    Optional<Department> getDepartmentById(UUID id);
}
