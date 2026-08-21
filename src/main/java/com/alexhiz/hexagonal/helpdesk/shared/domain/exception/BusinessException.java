package com.alexhiz.hexagonal.helpdesk.shared.domain.exception;

public class BusinessException extends DomainException {
    public BusinessException(String message) {
        super(message);
    }
}
