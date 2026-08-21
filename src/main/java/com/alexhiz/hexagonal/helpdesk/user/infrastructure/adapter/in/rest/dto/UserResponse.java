package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto.RoleResponse;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        UUID departmentId,
        Set<RoleResponse> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .departmentId(user.getDepartmentId())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream()
                                .map(RoleResponse::from)
                                .collect(Collectors.toSet())
                        : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
