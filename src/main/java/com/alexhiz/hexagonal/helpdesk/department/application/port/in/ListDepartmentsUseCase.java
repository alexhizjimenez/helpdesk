package com.alexhiz.hexagonal.helpdesk.department.application.port.in;

import java.util.List;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;

public interface ListDepartmentsUseCase {
    List<Department> getAllDepartments();
}
