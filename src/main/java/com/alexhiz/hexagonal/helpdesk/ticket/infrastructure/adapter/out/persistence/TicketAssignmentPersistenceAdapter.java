package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketAssignmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketAssignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TicketAssignmentPersistenceAdapter implements TicketAssignmentRepositoryPort {
    private final TicketAssignmentRepository ticketAssignmentRepository;
    private final TicketAssignmentPersistenceMapper ticketAssignmentPersistenceMapper;

    @Override
    public TicketAssignment saveTicketAssignment(TicketAssignment ticketAssignment) {
        TicketAssignmentEntity tay =  ticketAssignmentRepository.save(ticketAssignmentPersistenceMapper.toEntity(ticketAssignment));
        return ticketAssignmentPersistenceMapper.toDomain(tay);
    }
}
