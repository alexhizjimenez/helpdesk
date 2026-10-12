package com.alexhiz.hexagonal.helpdesk.ticket.application.port.in;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;

public interface CreateTicketUseCase {
    Ticket createTicket(Ticket ticket);
}
