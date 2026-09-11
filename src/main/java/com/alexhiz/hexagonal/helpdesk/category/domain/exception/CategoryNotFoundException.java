package com.alexhiz.hexagonal.helpdesk.category.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceNotFoundException;

import java.util.UUID;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(UUID id) {
        super("Category not found with id: "+ id);
    }
    public CategoryNotFoundException(String name) {
        super("Category not found with name: "+ name);
    }
}
