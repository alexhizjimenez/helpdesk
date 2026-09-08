package com.alexhiz.hexagonal.helpdesk.category.application.service;

import com.alexhiz.hexagonal.helpdesk.category.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.category.application.port.out.CategoryRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService implements CreateCategoryUseCase, UpdateCategoryUseCase, GetCategoryByIdUseCase, ListCategoryUseCase, DeleteCategoryUseCase  {
    private final CategoryRepositoryPort categoryRepositoryPort;

    @Override
    public Category create(Category category) {
        return categoryRepositoryPort.save(category);
    }

    @Override
    public void delete(UUID id) {
        categoryRepositoryPort.findById(id).orElseThrow(()-> new BusinessException("Categoria no encontrada"+id));
        categoryRepositoryPort.delete(id);
    }

    @Override
    public Optional<Category> getCategoryById(UUID id) {
        return categoryRepositoryPort.findById(id);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepositoryPort.findAll();
    }

    @Override
    public Category update(UUID id, Category category) {
        Category cat = categoryRepositoryPort.findById(id).orElseThrow(()-> new BusinessException("Categoria no encontrada"+id));
        cat.setName(category.getName());
        cat.setDepartmentId(category.getDepartmentId());
        cat.setActive(category.getActive());
        return categoryRepositoryPort.save(cat);
    }
}
