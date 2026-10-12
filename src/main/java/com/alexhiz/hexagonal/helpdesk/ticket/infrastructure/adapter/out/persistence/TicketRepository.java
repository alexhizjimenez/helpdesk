package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
    boolean existsByTicketNumber(String ticketNumber);


    @Query(
            value = """
        SELECT
            t.id AS id,
            t.title AS title,
            t.ticket_number AS ticketNumber,
            t.description AS description,
            t.priority AS priority,
            t.status AS status,
            c.name AS category,
            d.name AS department
        FROM tickets t
        LEFT JOIN categories c ON c.id = t.category_id
        LEFT JOIN departments d ON d.id = t.department_id
        ORDER BY t.created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM tickets t
        LEFT JOIN categories c ON c.id = t.category_id
        LEFT JOIN departments d ON d.id = t.department_id
        """,
            nativeQuery = true
    )
    Page<TicketListProjection> findTicketList(Pageable pageable);
}
