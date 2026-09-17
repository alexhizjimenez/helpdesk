package com.alexhiz.hexagonal.helpdesk.sla.domain.exception;

import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceNotFoundException;

public class SlaNotFoundException extends ResourceNotFoundException {

    public SlaNotFoundException(String name) {
        super("Sla policy not found with name: " + name);
    }

    public SlaNotFoundException(UUID id) {
        super("Sla policy not found with id: " + id);
    }

}
