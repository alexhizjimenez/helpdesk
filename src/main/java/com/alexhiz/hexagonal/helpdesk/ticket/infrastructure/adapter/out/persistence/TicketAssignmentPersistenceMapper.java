package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketAssignment;
import org.springframework.stereotype.Component;

@Component
public class TicketAssignmentPersistenceMapper {

    TicketAssignment toDomain(TicketAssignmentEntity entity){
        if(entity == null) return null;
        return TicketAssignment.builder().id(entity.getId())
                .assignedAt(entity.getAssignedAt())
                .assignedTo(entity.getAssignedTo())
                .assignedBy(entity.getAssignedBy())
                .ticketId(entity.getTicket())
                .reason(entity.getReason())
                .unassignedAt(entity.getUnassignedAt())
                .assignedBy(entity.getAssignedBy()).build();
    }

    TicketAssignmentEntity toEntity(TicketAssignment ticketAssignment){
        if(ticketAssignment == null) return null;
        return TicketAssignmentEntity.builder()
                .id(ticketAssignment.getId())
                .reason(ticketAssignment.getReason())
                .assignedAt(ticketAssignment.getAssignedAt())
                .assignedBy(ticketAssignment.getAssignedBy())
                .assignedTo(ticketAssignment.getAssignedTo())
                .ticket(ticketAssignment.getTicketId())
                .unassignedAt(ticketAssignment.getUnassignedAt())
                .build();
    }
}
