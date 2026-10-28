package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketHistoryRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TicketHistoryPersistenceAdapter implements TicketHistoryRepositoryPort {
    private final TicketHistoryRepository ticketHistoryRepository;
    private final TicketHistoryPersistenceMapper ticketHistoryPersistenceMapper;


    @Override
    public TicketHistory createTicketHistory(TicketHistory ticketHistory) {
        return ticketHistoryPersistenceMapper.toDomain(ticketHistoryRepository.save(ticketHistoryPersistenceMapper.toEntity(ticketHistory)));
    }
}
