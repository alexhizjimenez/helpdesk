package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record CategoryResponse(UUID id, String name, DepartmentResponse department, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public  static CategoryResponse from(Category category, Department department){
        return CategoryResponse.builder()
                .id(category.getId()).name(category.getName())
                .department(department != null ? DepartmentResponse.from(department) : null)
                .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build();
    }
}
