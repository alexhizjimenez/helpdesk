package com.alexhiz.hexagonal.helpdesk.auth.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.auth.domain.model.Login;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest.dto.TicketResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


public record AuthLoginRequest(@NotBlank String username, @NotBlank String password) {

    public Login toDomain() {
        return Login.builder()
                .username(username)
                .password(password)
                .build();
    }
}
