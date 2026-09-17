package com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

import lombok.Builder;

@Builder
public record SlaResponse(
        UUID id,
        String name,
        String priority,
        String responseTimeMinutes,
        String resolutionTimeMinutes,
        String active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static SlaResponse from(Sla sla) {
        return SlaResponse.builder().id(sla.getId()).name(sla.getName()).priority(sla.getPriority())
                .resolutionTimeMinutes(sla.getResolutionTimeMinutes()).responseTimeMinutes(sla.getResponseTimeMinutes())
                .active(sla.getActive()).createdAt(sla.getCreatedAt()).updatedAt(sla.getUpdatedAt()).build();
    }
}
