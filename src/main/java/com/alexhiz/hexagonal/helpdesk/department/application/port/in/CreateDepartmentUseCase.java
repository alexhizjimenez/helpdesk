package com.alexhiz.hexagonal.helpdesk.department.application.port.in;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;

public interface CreateDepartmentUseCase {
    Department create(Department department);
}
