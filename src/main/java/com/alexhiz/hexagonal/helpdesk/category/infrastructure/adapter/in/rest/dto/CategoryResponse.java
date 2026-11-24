package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CategoryResponse(UUID id, String name, DepartmentResponse department, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
    public  static CategoryResponse from(Category category, Department department){
        return CategoryResponse.builder()
                .id(category.getPublicId())
                .name(category.getName())
                .department(department != null ? DepartmentResponse.from(department) : null)
                .active(category.getActive())
                .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt())
                .deletedAt(category.getDeletedAt()).build();
    }
}
