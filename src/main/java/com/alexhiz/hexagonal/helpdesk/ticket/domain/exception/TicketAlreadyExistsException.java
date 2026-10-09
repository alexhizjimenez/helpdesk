package com.alexhiz.hexagonal.helpdesk.ticket.domain.exception;

import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.ResourceAlreadyExistsException;

public class TicketAlreadyExistsException extends ResourceAlreadyExistsException {
    public TicketAlreadyExistsException(String ticketNumber) {
        super("Ticket already exists with ticketNumber: " + ticketNumber);
    }
}
