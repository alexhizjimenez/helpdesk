package com.alexhiz.hexagonal.helpdesk.ticket.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceNotFoundException;

import java.util.UUID;

public class TicketAssignmentNotFoundException extends ResourceNotFoundException {
    public TicketAssignmentNotFoundException(UUID id) {
        super("Ticket assignment with id " + id + " not found");
    }
}
