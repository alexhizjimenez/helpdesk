package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record CategoryResponse(UUID id, String name, UUID departmentId, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public  static CategoryResponse from(Category category){
        return CategoryResponse.builder()
                .id(category.getId()).name(category.getName()).departmentId(category.getDepartmentId()).active(category.getActive())
                .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build();
    }
}
