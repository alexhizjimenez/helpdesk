package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketListItem;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketPriority;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TicketResponse(
        UUID id,
        String title,
        String ticketNumber,
        String description,
        TicketPriority priority,
        TicketStatus status,
        String category,
        String department
) {
    public static TicketResponse from(Ticket ticket) {
        return from(ticket, null, null);
    }

    public static TicketResponse from(Ticket ticket, String category, String department) {
        if (ticket == null) return null;
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .ticketNumber(ticket.getTicketNumber())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .category(category)
                .department(department)
                .build();
    }

    public static TicketResponse from(TicketListItem item) {
        if (item == null) return null;
        return TicketResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .ticketNumber(item.getTicketNumber())
                .description(item.getDescription())
                .priority(item.getPriority())
                .status(item.getStatus())
                .category(item.getCategory())
                .department(item.getDepartment())
                .build();
    }
}
