package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public PageResult<TicketListItem> findAllPages(PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        Page<TicketListProjection> entityPage = ticketRepository.findTicketList(pageable);
        List<TicketListItem> list = entityPage.getContent().stream()
                .map(ticketPersistenceMapper::toDomain)
                .toList();
        return new PageResult<>(
                list,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return ticketRepository.findById(id).map(ticketPersistenceMapper::toDomain);
    }
}
