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
public class Ticket {
    @EqualsAndHashCode.Include
    private UUID id;

    private String ticketNumber;

    private String title;

    private String description;

    private String status;

    private String priority;

    private UUID categoryId;

    private UUID departmentId;

    private UUID createdBy;

    private UUID assignedTo;

    private UUID slaPolicyId;

    private LocalDateTime openedAt;
    private LocalDateTime assignedAt;

    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    private LocalDateTime dueAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
