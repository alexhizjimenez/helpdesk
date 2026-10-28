package com.alexhiz.hexagonal.helpdesk.ticket.application.port.in;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketHistory;

import java.util.UUID;

public interface CreateTicketHistoryUseCase {

    TicketHistory createTicketHistory(UUID ticketId, TicketHistory ticketHistory);
}
