package com.alexhiz.hexagonal.helpdesk.department.application.port.in;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;

import java.util.UUID;

public interface RestoreDepartmentUseCase {
    Department restore(UUID id);
}
