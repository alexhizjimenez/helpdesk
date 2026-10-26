package com.alexhiz.hexagonal.helpdesk.ticket.application.port.in;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketStatus;

import java.util.UUID;

public interface AssignTicketUseCase {
    Ticket assignTicket(UUID ticketId, UUID assignId);
}
