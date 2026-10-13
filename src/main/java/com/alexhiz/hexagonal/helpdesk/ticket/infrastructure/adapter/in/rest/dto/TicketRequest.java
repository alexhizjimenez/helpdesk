package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketPriority;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TicketRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,

        @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
        String description,

        @NotNull(message = "Priority is required")
        TicketPriority priority,

        
        @NotNull(message = "Category is required")
        UUID categoryId,

        @NotNull(message = "Department is required")
        UUID departmentId
) {
        public Ticket toDomain() {
                return Ticket.builder()
                        .title(title)
                        .ticketNumber("TCK-"+ UUID.randomUUID().toString().toUpperCase())
                        .priority(priority)
                        .status(TicketStatus.OPEN)
                        .description(description)
                        .categoryId(categoryId)
                        .departmentId(departmentId)
                        .build();
        }
}
