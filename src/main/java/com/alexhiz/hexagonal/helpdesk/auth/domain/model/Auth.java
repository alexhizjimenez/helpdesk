package com.alexhiz.hexagonal.helpdesk.auth.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Auth {
    @EqualsAndHashCode.Include
    private UUID id;
    private String email;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
