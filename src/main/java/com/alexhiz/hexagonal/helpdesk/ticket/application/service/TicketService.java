package com.alexhiz.hexagonal.helpdesk.ticket.application.service;

import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.out.SlaRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.sla.domain.exception.SlaNotFoundException;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.SlaPriotity;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.AssignTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.ChangeStatusTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.CreateTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.PageTicketsUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketAssignmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.exception.TicketAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.exception.TicketNotFoundException;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.*;
import com.alexhiz.hexagonal.helpdesk.user.application.port.out.UserRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.user.domain.exception.UserNotFoundException;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketService implements CreateTicketUseCase, PageTicketsUseCase, AssignTicketUseCase, ChangeStatusTicketUseCase {
    private final TicketRepositoryPort ticketRepositoryPort;
    private final SlaRepositoryPort slaRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final TicketAssignmentRepositoryPort ticketAssignmentRepositoryPort;

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

    @Override
    public Ticket assignTicket(UUID ticketId, UUID assignId) {
        Ticket ticket = ticketRepositoryPort.findById(ticketId).orElseThrow( () -> new TicketNotFoundException(ticketId));
        ticket.setAssignedTo(assignId);
        ticket.setAssignedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        Ticket savedTicket = ticketRepositoryPort.saveTicket(ticket);
        //crear ticket_assigment
        TicketAssignment ticketAssignment = new TicketAssignment();
        ticketAssignment.setAssignedAt(LocalDateTime.now());
        ticketAssignment.setAssignedBy(savedTicket.getCreatedBy());
        ticketAssignment.setReason(savedTicket.getDescription());
        ticketAssignment.setTicketId(savedTicket.getId());
        ticketAssignment.setAssignedTo(savedTicket.getAssignedTo());
        ticketAssignmentRepositoryPort.saveTicketAssignment(ticketAssignment);
        return savedTicket;
    }

    @Override
    public Ticket changeStatusTicket(UUID ticketId, String status) {
        Ticket ticket = ticketRepositoryPort.findById(ticketId).orElseThrow( () -> new TicketNotFoundException(ticketId));
        log.info("el status es: {}", TicketStatus.valueOf(status.toUpperCase()));
        ticket.setStatus(TicketStatus.valueOf(status.toUpperCase()));
        return ticketRepositoryPort.saveTicket(ticket);
    }
}
