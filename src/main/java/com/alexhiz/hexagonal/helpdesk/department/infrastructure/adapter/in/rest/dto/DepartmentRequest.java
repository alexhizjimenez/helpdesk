package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
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

