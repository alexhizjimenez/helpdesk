package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import java.util.UUID;

public interface TicketListProjection {
    UUID getId();

    String getTitle();

    String getTicketNumber();

    String getDescription();

    String getPriority();

    String getStatus();

    String getCategory();

    String getDepartment();
}
