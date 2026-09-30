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
public class TicketComment {
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID ticketId;
    private UUID userId;
    private String comment;
    private boolean internal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
