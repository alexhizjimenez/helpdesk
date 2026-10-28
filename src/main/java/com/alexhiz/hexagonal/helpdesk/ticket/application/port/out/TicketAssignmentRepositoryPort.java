package com.alexhiz.hexagonal.helpdesk.ticket.application.port.out;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketAssignment;

import java.util.Optional;
import java.util.UUID;

public interface TicketAssignmentRepositoryPort {
    TicketAssignment saveTicketAssignment(TicketAssignment ticketAssignment);
    boolean existTicketAssignmentForTicketId(UUID id);
    Optional<TicketAssignment> getTicketAssignmentForTicketId(UUID id);
}
