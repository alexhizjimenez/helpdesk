package com.alexhiz.hexagonal.helpdesk.ticket.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceNotFoundException;

import java.util.UUID;

public class TicketNotFoundException extends ResourceNotFoundException {
    public TicketNotFoundException(String ticketNumber) {
        super("Ticket not found with ticketNumber:" + ticketNumber);
    }

    public TicketNotFoundException(UUID id) {
        super("Ticket not found with id:" + id);
    }
}
