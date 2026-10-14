package com.alexhiz.hexagonal.helpdesk.auth.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceNotFoundException;

public class AuthNotFoundException extends ResourceNotFoundException {
    public AuthNotFoundException(String username) {
        super("Username or Password Invalidate: " + username);
    }

}
