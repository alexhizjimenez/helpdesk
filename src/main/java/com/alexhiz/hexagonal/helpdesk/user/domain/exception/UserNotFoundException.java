package com.alexhiz.hexagonal.helpdesk.user.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
