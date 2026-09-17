package com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

@Component
public class SlaPersistenceMapper {
    SlaEntity toEntity(Sla sla) {
        return SlaEntity.builder().id(sla.getId()).name(sla.getName()).priority(sla.getPriority())
                .resolutionTimeMinutes(sla.getResolutionTimeMinutes()).responseTimeMinutes(sla.getResponseTimeMinutes())
                .active(sla.getActive()).createdAt(sla.getCreatedAt()).updatedAt(sla.getUpdatedAt()).build();
    }

    Sla toDomain(SlaEntity entity) {
        return Sla.builder().id(entity.getId()).name(entity.getName()).priority(entity.getPriority())
                .resolutionTimeMinutes(entity.getResolutionTimeMinutes())
                .responseTimeMinutes(entity.getResponseTimeMinutes())
                .active(entity.getActive()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }
}
