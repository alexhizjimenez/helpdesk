package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartmentRequest(
        @NotBlank(message = "Department name is required")
        String name,

        @NotNull(message = "Active status is required")
        Boolean active
) {
}
