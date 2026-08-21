package com.alexhiz.hexagonal.helpdesk.shared.domain.exception;

public class ResourceAlreadyExistsException extends DomainException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
