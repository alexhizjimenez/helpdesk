package com.alexhiz.hexagonal.helpdesk.department.application.service;

import com.alexhiz.hexagonal.helpdesk.department.application.port.in.CreateDepartmentUseCase;
import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService implements CreateDepartmentUseCase {

    private final DepartmentRepositoryPort departmentRepositoryPort;

    @Override
    public Department create(Department department) {
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new BusinessException("Department name cannot be empty");
        }

        if (departmentRepositoryPort.existsByName(department.getName().trim())) {
            throw new DepartmentAlreadyExistsException(department.getName().trim());
        }

        return departmentRepositoryPort.save(department);
    }
}
