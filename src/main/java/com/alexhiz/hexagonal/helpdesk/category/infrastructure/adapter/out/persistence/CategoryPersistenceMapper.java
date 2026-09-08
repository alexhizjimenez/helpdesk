package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence;


import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryPersistenceMapper {

    public Category toDomain(CategoryEntity entity){
        return Category.builder()
                .id(entity.getId()).name(entity.getName()).departmentId(entity.getDepartmentId()).active(entity.getActive())
                .createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }

    public CategoryEntity toEntity(Category category){
        return CategoryEntity.builder()
                .id(category.getId()).name(category.getName()).departmentId(category.getDepartmentId()).active(category.getActive())
                .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build();
    }
}
