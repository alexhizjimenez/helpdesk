package com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

import jakarta.validation.constraints.NotBlank;

public record SlaRequest(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "priority is required") String priority,
        @NotBlank(message = "responseTimeMinutes is required") String responseTimeMinutes,
        @NotBlank(message = "resolutionTimeMinutes is required") String resolutionTimeMinutes,
        String active) {
    public Sla toDomain() {
        return Sla.builder().name(name).priority(priority).resolutionTimeMinutes(resolutionTimeMinutes)
                .responseTimeMinutes(responseTimeMinutes).active(active).build();
    }
}
