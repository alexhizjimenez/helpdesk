package com.alexhiz.hexagonal.helpdesk.ticket.application.port.out;

import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketListItem;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepositoryPort {
    Ticket saveTicket(Ticket ticket);
    boolean existsByTicketNumber(String ticketNumber);
    PageResult<TicketListItem> findAllPages(PageQuery pageQuery);
    Optional<Ticket> findById(UUID id);
}
