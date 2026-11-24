package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DepartmentResponse(
        UUID id,
        String name,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static DepartmentResponse from(Department department) {
        return DepartmentResponse.builder()
                .id(department.getPublicId())
                .name(department.getName())
                .active(department.getActive())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .deletedAt(department.getDeletedAt())
                .build();
    }


}
