package com.alexhiz.hexagonal.helpdesk.ticket.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketListItem {
    private UUID id;
    private String title;
    private String ticketNumber;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private String category;
    private String department;
}
