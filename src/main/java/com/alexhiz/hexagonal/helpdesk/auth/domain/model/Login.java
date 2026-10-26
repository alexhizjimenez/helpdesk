package com.alexhiz.hexagonal.helpdesk.auth.domain.model;

import lombok.Builder;

@Builder
public record Login(
        String username,
        String password
) {
}
