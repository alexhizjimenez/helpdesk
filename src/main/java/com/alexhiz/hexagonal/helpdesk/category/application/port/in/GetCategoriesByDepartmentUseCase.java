package com.alexhiz.hexagonal.helpdesk.category.application.port.in;

import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;

import java.util.List;
import java.util.UUID;

public interface GetCategoriesByDepartmentUseCase {
    List<Category> getAllCategoriesByDepartment(UUID departmentId);
}
