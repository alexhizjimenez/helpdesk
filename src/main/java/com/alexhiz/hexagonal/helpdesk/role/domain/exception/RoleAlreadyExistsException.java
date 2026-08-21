package com.alexhiz.hexagonal.helpdesk.role.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceAlreadyExistsException;

public class RoleAlreadyExistsException extends ResourceAlreadyExistsException {
    public RoleAlreadyExistsException(String name) {
        super("Role already exists with name: " + name);
    }
}
