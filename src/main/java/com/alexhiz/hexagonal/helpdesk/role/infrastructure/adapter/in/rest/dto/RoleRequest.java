package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.RoleEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RoleRequest(
        @NotNull(message = "Role name is required")
        RoleEnum name
) {
    public Role toDomain() {
        return Role.builder()
                .name(name)
                .build();
    }
}

