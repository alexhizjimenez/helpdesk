package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RoleResponse(
        UUID id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static RoleResponse from(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
