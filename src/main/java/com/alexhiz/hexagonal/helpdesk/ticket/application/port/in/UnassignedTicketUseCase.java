package com.alexhiz.hexagonal.helpdesk.ticket.application.port.in;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;

import java.util.UUID;

public interface UnassignedTicketUseCase {
    Ticket unassignedTicket(UUID ticketId);
}
