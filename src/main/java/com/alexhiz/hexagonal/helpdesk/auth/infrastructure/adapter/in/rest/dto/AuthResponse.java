package com.alexhiz.hexagonal.helpdesk.auth.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.auth.domain.model.Auth;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@JsonPropertyOrder({"username", "message", "status", "jwt"})
@Builder
public record AuthResponse(
        String username,
        String message,
        String jwt,
        Boolean status
) {

    public static AuthResponse from(Auth auth){
        return AuthResponse.builder()
                .username(auth.username())
                .message(auth.message())
                .jwt(auth.accessToken())
                .status(auth.status())
                .build();
    }
}
