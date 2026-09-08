package com.alexhiz.hexagonal.helpdesk.category.application.port.in;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;

public interface CreateCategoryUseCase {
    Category create(Category category);
}
