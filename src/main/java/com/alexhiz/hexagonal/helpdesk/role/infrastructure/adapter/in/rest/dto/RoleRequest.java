package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest(
        @NotBlank(message = "Role name is required")
        String name
) {
}
