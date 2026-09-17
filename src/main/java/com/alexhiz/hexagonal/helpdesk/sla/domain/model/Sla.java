package com.alexhiz.hexagonal.helpdesk.sla.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sla {
    @EqualsAndHashCode.Include
    private UUID id;
    private String name;
    private String priority;
    private String responseTimeMinutes;
    private String resolutionTimeMinutes;
    private String active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
