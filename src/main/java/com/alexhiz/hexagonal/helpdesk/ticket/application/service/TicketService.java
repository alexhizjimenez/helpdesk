package com.alexhiz.hexagonal.helpdesk.ticket.application.service;

import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.CreateTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.exception.TicketAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService implements CreateTicketUseCase {
    private final TicketRepositoryPort ticketRepositoryPort;

    @Override
    public Ticket createTicket(Ticket ticket) {
        log.info("Creating a new ticket : {}", ticket);

        if(ticketRepositoryPort.existsByTicketNumber(ticket.getTicketNumber())){
            throw new TicketAlreadyExistsException(ticket.getTicketNumber());
        }

       return ticketRepositoryPort.saveTicket(ticket);
    }
}
