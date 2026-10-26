package com.alexhiz.hexagonal.helpdesk.ticket.application.port.out;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketAssignment;

public interface TicketAssignmentRepositoryPort {
    TicketAssignment saveTicketAssignment(TicketAssignment ticketAssignment);
}
