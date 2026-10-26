package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketAssignmentRepository extends JpaRepository<TicketAssignmentEntity, UUID> {
}
