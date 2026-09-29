package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.RoleEnum;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Builder
public record RoleResponse(
        UUID id,
        RoleEnum name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<PermissionResponse> permissions

) {

    public static RoleResponse from(Role role) {
        log.info("Role permissions: {}", role.getPermissions());
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .permissions(role.getPermissions() == null
                        ? Set.of()
                        : role.getPermissions()
                        .stream()
                        .map(PermissionResponse::from)
                        .collect(Collectors.toSet()))
                .build();
    }
}
