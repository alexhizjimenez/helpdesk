package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="ticket_id")
    private TicketEntity ticket;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="assigned_to")
    private UserEntity assignedTo;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="assigned_by")
    private UserEntity assignedBy;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;
    @Column(name = "unassigned_at", nullable = false, updatable = false)
    private LocalDateTime unassignedAt;
    private String reason;
}
