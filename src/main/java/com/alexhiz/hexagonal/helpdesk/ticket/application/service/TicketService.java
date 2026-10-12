package com.alexhiz.hexagonal.helpdesk.ticket.application.service;

import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.out.SlaRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.sla.domain.exception.SlaNotFoundException;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.SlaPriotity;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.CreateTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.PageTicketsUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.exception.TicketAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketListItem;
import com.alexhiz.hexagonal.helpdesk.user.application.port.out.UserRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.user.domain.exception.UserNotFoundException;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService implements CreateTicketUseCase, PageTicketsUseCase {
    private final TicketRepositoryPort ticketRepositoryPort;
    private final SlaRepositoryPort slaRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    private final ObjectMapper objectMapper;


    @Override
    public Ticket createTicket(Ticket ticket) {
        try {
            log.info("Creating a new ticket : {}", objectMapper.writeValueAsString(ticket));
        } catch (Exception e) {
            log.warn("Could not serialize ticket", e);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) ? "anonymous" : auth.getName();
        User user = userRepositoryPort.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User not found:" + username));
        Sla sla = slaRepositoryPort.getByPriority(SlaPriotity.valueOf(ticket.getPriority().name())).orElseThrow( () -> new SlaNotFoundException("Sla Policy not found"+ ticket.getPriority().name()));


        if(ticketRepositoryPort.existsByTicketNumber(ticket.getTicketNumber())){
            throw new TicketAlreadyExistsException(ticket.getTicketNumber());
        }
        ticket.setSlaPolicyId(sla.getId());
        ticket.setAssignedAt(LocalDateTime.now());
        ticket.setCreatedBy(user.getId());
        return ticketRepositoryPort.saveTicket(ticket);
    }

    @Override
    public PageResult<TicketListItem> execute(PageQuery pageQuery) {
        return ticketRepositoryPort.findAllPages(pageQuery);
    }
}
