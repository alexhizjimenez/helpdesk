package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TicketPersistenceAdapter implements TicketRepositoryPort {
    private final TicketPersistenceMapper ticketPersistenceMapper;
    private final TicketRepository ticketRepository;

    @Override
    public Ticket saveTicket(Ticket ticket) {
        log.info("{}:", ticket.toString());
        TicketEntity ticketEntity = ticketRepository.save(ticketPersistenceMapper.toEntity(ticket));
        return ticketPersistenceMapper.toDomain(ticketEntity);
    }

    @Override
    public boolean existsByTicketNumber(String ticketNumber) {
        return ticketRepository.existsByTicketNumber(ticketNumber);
    }
}
