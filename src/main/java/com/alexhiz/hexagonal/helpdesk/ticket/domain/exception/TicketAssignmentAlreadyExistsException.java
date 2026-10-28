package com.alexhiz.hexagonal.helpdesk.ticket.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceAlreadyExistsException;

import java.util.UUID;

public class TicketAssignmentAlreadyExistsException extends ResourceAlreadyExistsException {
    public TicketAssignmentAlreadyExistsException(UUID ticketId) {
        super("Ticket assignment with id " + ticketId + " already exists.");
    }
}
