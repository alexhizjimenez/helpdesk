package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.CreateTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest.dto.TicketRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final CreateTicketUseCase createTicketUseCase;

    @PostMapping
    public ResponseEntity<Void> createTicket(@Valid @RequestBody TicketRequest request){
        createTicketUseCase.createTicket(request.toDomain());
        return null;
    }
}
