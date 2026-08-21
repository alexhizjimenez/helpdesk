package com.alexhiz.hexagonal.helpdesk.role.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceNotFoundException;

import java.util.UUID;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(UUID id) {
        super("Role not found with id: " + id);
    }

    public RoleNotFoundException(String name) {
        super("Role not found with name: " + name);
    }
}
