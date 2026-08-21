package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentPersistenceMapper {

    public Department toDomain(DepartmentEntity departmentEntity) {
        if (departmentEntity == null) return null;
        return Department.builder()
                .id(departmentEntity.getId())
                .name(departmentEntity.getName())
                .active(departmentEntity.getActive())
                .createdAt(departmentEntity.getCreatedAt())
                .updatedAt(departmentEntity.getUpdatedAt())
                .build();
    }

    public DepartmentEntity toEntity(Department department) {
        if (department == null) return null;
        return DepartmentEntity.builder()
                .id(department.getId())
                .name(department.getName())
                .active(department.getActive())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}
