package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartmentRequest(
        @NotBlank(message = "Department name is required")
        String name,

        @NotNull(message = "Active status is required")
        Boolean active
) {
    public Department toDomain() {
        return Department.builder()
                .name(name)
                .active(active)
                .build();
    }
}

