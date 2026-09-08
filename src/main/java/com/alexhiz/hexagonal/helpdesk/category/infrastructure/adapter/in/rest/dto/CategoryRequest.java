package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;


public record CategoryRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 3, message = "Al menos 3 caracteres para el nombre")
        String name,

        @NotNull
        UUID departmentId,

        @NotNull
        Boolean active) {
    
    public Category toDomain(){
        return Category.builder()
                .name(name).departmentId(departmentId).active(active).build();
    }
}
