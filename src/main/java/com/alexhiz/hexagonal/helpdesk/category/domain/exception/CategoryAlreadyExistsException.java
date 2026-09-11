package com.alexhiz.hexagonal.helpdesk.category.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceAlreadyExistsException;

public class CategoryAlreadyExistsException extends ResourceAlreadyExistsException {
    public CategoryAlreadyExistsException(String name) {
        super("Category already exists with name: "+ name);
    }
}
