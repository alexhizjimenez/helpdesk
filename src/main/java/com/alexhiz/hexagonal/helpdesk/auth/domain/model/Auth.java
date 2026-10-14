package com.alexhiz.hexagonal.helpdesk.auth.domain.model;

public record Auth(
        String username,
        String message,
        String accessToken,
        boolean status
)
{

}
