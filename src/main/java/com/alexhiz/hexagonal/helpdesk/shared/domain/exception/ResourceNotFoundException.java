package com.alexhiz.hexagonal.helpdesk.shared.domain.exception;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
