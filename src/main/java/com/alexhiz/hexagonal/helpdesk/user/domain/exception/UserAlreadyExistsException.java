package com.alexhiz.hexagonal.helpdesk.user.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceAlreadyExistsException;

public class UserAlreadyExistsException extends ResourceAlreadyExistsException {
    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
    }
}
