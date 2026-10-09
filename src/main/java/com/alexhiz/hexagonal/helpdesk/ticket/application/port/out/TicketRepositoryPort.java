package com.alexhiz.hexagonal.helpdesk.ticket.application.port.out;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;

public interface TicketRepositoryPort {
    Ticket saveTicket(Ticket ticket);
    boolean existsByTicketNumber(String ticketNumber);

}
