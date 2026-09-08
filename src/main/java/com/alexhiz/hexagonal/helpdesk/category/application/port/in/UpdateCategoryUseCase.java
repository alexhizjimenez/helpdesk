package com.alexhiz.hexagonal.helpdesk.category.application.port.in;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;

import java.util.UUID;

public interface UpdateCategoryUseCase {
    Category update(UUID id, Category category);
}
