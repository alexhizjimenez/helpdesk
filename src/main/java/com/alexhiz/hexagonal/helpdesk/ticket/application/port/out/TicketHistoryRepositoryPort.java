package com.alexhiz.hexagonal.helpdesk.ticket.application.port.out;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketHistory;

public interface TicketHistoryRepositoryPort {
    TicketHistory createTicketHistory(TicketHistory ticketHistory);
}
