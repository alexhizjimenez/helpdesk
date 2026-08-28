package com.alexhiz.hexagonal.helpdesk.department.application.port.in;

import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;

public interface UpdateDepartmentUseCase {
   Department updateDepartment(UUID id, Department department);
}
