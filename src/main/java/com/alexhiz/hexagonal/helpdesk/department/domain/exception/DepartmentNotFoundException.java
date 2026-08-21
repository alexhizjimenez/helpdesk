package com.alexhiz.hexagonal.helpdesk.department.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceNotFoundException;

import java.util.UUID;

public class DepartmentNotFoundException extends ResourceNotFoundException {
    public DepartmentNotFoundException(UUID id) {
        super("Department not found with id: " + id);
    }

    public DepartmentNotFoundException(String name) {
        super("Department not found with name: " + name);
    }
}
