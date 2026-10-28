package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketAssignmentRepository extends JpaRepository<TicketAssignmentEntity, UUID> {

    boolean existsByTicket(UUID ticketId);
    Optional<TicketAssignmentEntity> findTicketAssignmentByTicket(UUID ticketId);
}
