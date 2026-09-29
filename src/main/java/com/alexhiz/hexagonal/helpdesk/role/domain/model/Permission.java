package com.alexhiz.hexagonal.helpdesk.role.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Permission {
    @EqualsAndHashCode.Include
    private UUID id;
    private PermissionEnum name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
