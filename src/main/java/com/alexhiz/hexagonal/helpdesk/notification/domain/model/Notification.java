package com.alexhiz.hexagonal.helpdesk.notification.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {
    @EqualsAndHashCode.Include
    private UUID id;
    private String recipient;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
