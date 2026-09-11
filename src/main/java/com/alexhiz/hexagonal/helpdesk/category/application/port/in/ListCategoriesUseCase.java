package com.alexhiz.hexagonal.helpdesk.category.application.port.in;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;

import java.util.List;

public interface ListCategoriesUseCase {
    List<Category> getAllCategories();
}
