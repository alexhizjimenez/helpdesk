package com.alexhiz.hexagonal.helpdesk.department.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceAlreadyExistsException;

public class DepartmentAlreadyExistsException extends ResourceAlreadyExistsException {
    public DepartmentAlreadyExistsException(String name) {
        super("Department already exists with name: " + name);
    }
}
