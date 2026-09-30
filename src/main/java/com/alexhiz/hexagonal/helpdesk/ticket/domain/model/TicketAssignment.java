package com.alexhiz.hexagonal.helpdesk.ticket.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TicketAssignment {
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID ticketId;
    private UUID assignedTo;
    private UUID assignedBy;
    private LocalDateTime assignedAt;
    private LocalDateTime unassignedAt;
    private String reason;
}
