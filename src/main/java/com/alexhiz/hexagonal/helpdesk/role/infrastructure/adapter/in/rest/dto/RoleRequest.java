package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import jakarta.validation.constraints.NotBlank;

public record RoleRequest(
        @NotBlank(message = "Role name is required")
        String name
) {
    public Role toDomain() {
        return Role.builder()
                .name(name)
                .build();
    }
}

