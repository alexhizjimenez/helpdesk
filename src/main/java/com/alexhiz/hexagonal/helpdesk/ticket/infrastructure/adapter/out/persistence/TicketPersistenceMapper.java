package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.SlaPriotity;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketListItem;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketPriority;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketStatus;
import org.springframework.stereotype.Component;

@Component
public class TicketPersistenceMapper {

    public TicketEntity toEntity(Ticket ticket){
        if(ticket == null) return null;
        return TicketEntity.builder()
                .id(ticket.getId()).ticketNumber(ticket.getTicketNumber()).title(ticket.getTitle())
                .description(ticket.getDescription()).status(ticket.getStatus()).priority(TicketPriority.valueOf(ticket.getPriority().toString())).categoryId(ticket.getCategoryId())
                .departmentId(ticket.getDepartmentId()).createdBy(ticket.getCreatedBy()).assignedTo(ticket.getAssignedTo()).slaPolicyId(ticket.getSlaPolicyId())
                .openedAt(ticket.getOpenedAt()).assignedAt(ticket.getAssignedAt()).resolvedAt(ticket.getResolvedAt()).closedAt(ticket.getClosedAt())
                .dueAt(ticket.getDueAt()).createdAt(ticket.getCreatedAt()).updatedAt(ticket.getUpdatedAt()).build();

    }

    public Ticket toDomain(TicketEntity ticketEntity){
        if(ticketEntity == null) return null;
        return Ticket.builder().id(ticketEntity.getId()).ticketNumber(ticketEntity.getTicketNumber()).title(ticketEntity.getTitle())
                .description(ticketEntity.getDescription()).status(ticketEntity.getStatus()).priority(TicketPriority.valueOf(ticketEntity.getPriority().toString()))
                .categoryId(ticketEntity.getCategoryId()).departmentId(ticketEntity.getDepartmentId()).createdBy(ticketEntity.getCreatedBy())
                .assignedTo(ticketEntity.getAssignedTo()).slaPolicyId(ticketEntity.getSlaPolicyId()).openedAt(ticketEntity.getOpenedAt())
                .assignedAt(ticketEntity.getAssignedAt()).resolvedAt(ticketEntity.getResolvedAt()).closedAt(ticketEntity.getClosedAt())
                .dueAt(ticketEntity.getDueAt()).createdAt(ticketEntity.getCreatedAt()).updatedAt(ticketEntity.getUpdatedAt()).build();
    }

    public TicketListItem toDomain(TicketListProjection projection) {
        if (projection == null) return null;

        TicketPriority priority = null;
        if (projection.getPriority() != null) {
            try {
                priority = TicketPriority.valueOf(projection.getPriority().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        TicketStatus status = null;
        if (projection.getStatus() != null) {
            try {
                status = TicketStatus.valueOf(projection.getStatus().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return TicketListItem.builder()
                .id(projection.getId())
                .title(projection.getTitle())
                .ticketNumber(projection.getTicketNumber())
                .description(projection.getDescription())
                .priority(priority)
                .status(status)
                .category(projection.getCategory())
                .department(projection.getDepartment())
                .build();
    }
}
