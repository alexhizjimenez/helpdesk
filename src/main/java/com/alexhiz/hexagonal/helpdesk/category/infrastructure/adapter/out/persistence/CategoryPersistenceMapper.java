package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence;


import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence.DepartmentEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryPersistenceMapper {
    private final EntityManager entityManager;

    public Category toDomain(CategoryEntity entity){
        if (entity == null) return null;
        return Category.builder()
                .id(entity.getId()).name(entity.getName())
                .departmentId(entity.getDepartment() != null ? entity.getDepartment().getId() : null)
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }

    public CategoryEntity toEntity(Category category){
        if (category == null) return null;
        // Creamos una referencia al DepartmentEntity usando solo su ID (sin hacer SELECT a la BD)
        DepartmentEntity departmentEntity = null;
        if (category.getDepartmentId() != null) {
            departmentEntity = entityManager.getReference(DepartmentEntity.class, category.getDepartmentId());
        }
        return CategoryEntity.builder()
                .id(category.getId()).name(category.getName())
                .department(departmentEntity)
                .active(category.getActive())
                .createdAt(category.getCreatedAt()).updatedAt(category.getUpdatedAt()).build();
    }
}
