package com.alexhiz.hexagonal.helpdesk.ticket.domain.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TicketAttachment {
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID ticketId;
    private UUID uploadedby;
    private String fileName;
    private String filePath;
    private String contentType;
    private String fileSize;
    private String createdAt;
    private String updatedAt;
}
