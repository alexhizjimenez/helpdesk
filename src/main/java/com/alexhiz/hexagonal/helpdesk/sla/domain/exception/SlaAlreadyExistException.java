package com.alexhiz.hexagonal.helpdesk.sla.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceAlreadyExistsException;

public class SlaAlreadyExistException extends ResourceAlreadyExistsException {

    public SlaAlreadyExistException(String name) {
        super("Sla already exists with name: " + name);
    }

}
