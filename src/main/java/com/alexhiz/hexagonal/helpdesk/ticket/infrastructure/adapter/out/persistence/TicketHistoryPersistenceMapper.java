package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketHistory;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketHistoryPersistenceMapper {

    TicketHistory toDomain(TicketHistoryEntity ticketHistoryEntity) {
        if(ticketHistoryEntity == null) return null;
        return TicketHistory.builder()
                .id(ticketHistoryEntity.getId())
                .ticketId(ticketHistoryEntity.getTicket().getId())
                .userId(ticketHistoryEntity.getUser().getId())
                .action(ticketHistoryEntity.getAction())
                .description(ticketHistoryEntity.getDescription())
                .oldValue(ticketHistoryEntity.getOldValue())
                .newValue(ticketHistoryEntity.getNewValue())
                .createdAt(ticketHistoryEntity.getCreatedAt())
                .updatedAt(ticketHistoryEntity.getUpdatedAt())
                .build();
    }

    TicketHistoryEntity toEntity(TicketHistory ticketHistory) {
        if(ticketHistory == null) return null;
        return TicketHistoryEntity.builder()
                .id(ticketHistory.getId())
                .ticket(ticketHistory.getTicketId() != null
                        ? TicketEntity.builder().id(ticketHistory.getTicketId()).build()
                        : null)
                .user(ticketHistory.getUserId() != null
                        ? UserEntity.builder().id(ticketHistory.getUserId()).build()
                        : null)
                .action(ticketHistory.getAction())
                .description(ticketHistory.getDescription())
                .oldValue(ticketHistory.getOldValue())
                .newValue(ticketHistory.getNewValue())
                .createdAt(ticketHistory.getCreatedAt())
                .updatedAt(ticketHistory.getUpdatedAt())
                .build();
    }
}
