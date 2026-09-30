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
public class TicketHistory {
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID ticketId;
    private UUID userId;
    //cambiarlo despues a ENUM
    private String action;
    private String oldValue;
    private String newValue;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
