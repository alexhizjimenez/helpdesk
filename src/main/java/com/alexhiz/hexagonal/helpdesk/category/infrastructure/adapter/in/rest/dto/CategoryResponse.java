package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryResponse(UUID id, String name, UUID departmentId, Boolean active) {
    public  static CategoryResponse from(Category category){
        return CategoryResponse.builder()
                .id(category.getId()).name(category.getName()).departmentId(category.getDepartmentId()).active(category.getActive()).build();
    }
}
