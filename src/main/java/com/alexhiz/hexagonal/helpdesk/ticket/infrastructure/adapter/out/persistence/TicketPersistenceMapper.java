package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketPersistenceMapper {

    public TicketEntity toEntity(Ticket ticket){
        if(ticket == null) return null;
        return TicketEntity.builder()
                .id(ticket.getId()).ticketNumber(ticket.getTicketNumber()).title(ticket.getTitle())
                .description(ticket.getDescription()).status(ticket.getStatus()).priority(ticket.getPriority()).categoryId(ticket.getCategoryId())
                .departmentId(ticket.getDepartmentId()).createdBy(ticket.getCreatedBy()).assignedTo(ticket.getAssignedTo()).slaPolicyId(ticket.getSlaPolicyId())
                .openedAt(ticket.getOpenedAt()).assignedAt(ticket.getAssignedAt()).resolvedAt(ticket.getResolvedAt()).closedAt(ticket.getClosedAt())
                .dueAt(ticket.getDueAt()).createdAt(ticket.getCreatedAt()).updatedAt(ticket.getUpdatedAt()).build();

    }

    public Ticket toDomain(TicketEntity ticketEntity){
        if(ticketEntity == null) return null;
        return Ticket.builder().id(ticketEntity.getId()).ticketNumber(ticketEntity.getTicketNumber()).title(ticketEntity.getTitle())
                .description(ticketEntity.getDescription()).status(ticketEntity.getStatus()).priority(ticketEntity.getPriority())
                .categoryId(ticketEntity.getCategoryId()).departmentId(ticketEntity.getDepartmentId()).createdBy(ticketEntity.getCreatedBy())
                .assignedTo(ticketEntity.getAssignedTo()).slaPolicyId(ticketEntity.getSlaPolicyId()).openedAt(ticketEntity.getOpenedAt())
                .assignedAt(ticketEntity.getAssignedAt()).resolvedAt(ticketEntity.getResolvedAt()).closedAt(ticketEntity.getClosedAt())
                .dueAt(ticketEntity.getDueAt()).createdAt(ticketEntity.getCreatedAt()).updatedAt(ticketEntity.getUpdatedAt()).build();
    }
}
