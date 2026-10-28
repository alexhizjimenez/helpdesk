package com.alexhiz.hexagonal.helpdesk.ticket.application.service;

import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.out.SlaRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.sla.domain.exception.SlaNotFoundException;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.SlaPriotity;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketAssignmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketHistoryRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.out.TicketRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.exception.TicketAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.exception.TicketAssignmentAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.exception.TicketAssignmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.exception.TicketNotFoundException;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.*;
import com.alexhiz.hexagonal.helpdesk.user.application.port.out.UserRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.user.domain.exception.UserNotFoundException;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
public class TicketService implements CreateTicketUseCase, PageTicketsUseCase, AssignTicketUseCase, ChangeStatusTicketUseCase, CreateTicketHistoryUseCase, UnassignedTicketUseCase {
    private final TicketRepositoryPort ticketRepositoryPort;
    private final SlaRepositoryPort slaRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final TicketAssignmentRepositoryPort ticketAssignmentRepositoryPort;
    private final TicketHistoryRepositoryPort ticketHistoryRepositoryPort;

    @Override
    public Ticket createTicket(Ticket ticket) {
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

        Ticket savedTicket = ticketRepositoryPort.saveTicket(ticket);
        saveTicketHistory(TicketHistory.builder()
                .ticketId(savedTicket.getId())
                .userId(user.getId())
                .action("CREATED")
                .description("Ticket created with status: " + savedTicket.getStatus())
                .oldValue(savedTicket.getStatus().name())
                .newValue(savedTicket.getStatus().name())
                .build());

        return savedTicket;
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
        if(ticketAssignmentRepositoryPort.existTicketAssignmentForTicketId(savedTicket.getId())){
            throw new TicketAssignmentAlreadyExistsException(savedTicket.getId());
        }
        saveTicketAssigment(TicketAssignment.builder().ticketId(savedTicket.getId()).assignedTo(savedTicket.getAssignedTo())
                .assignedBy(savedTicket.getCreatedBy()).assignedAt(LocalDateTime.now())
                .reason(savedTicket.getDescription()).build());
        return savedTicket;
    }

    @Override
    public Ticket changeStatusTicket(UUID ticketId, String status) {
        Ticket ticket = ticketRepositoryPort.findById(ticketId).orElseThrow( () -> new TicketNotFoundException(ticketId));
        log.info("el status es: {}", TicketStatus.valueOf(status.toUpperCase()));
        ticket.setStatus(TicketStatus.valueOf(status.toUpperCase()));
        return ticketRepositoryPort.saveTicket(ticket);
    }


    @Override
    public TicketHistory createTicketHistory(UUID ticketId, @NonNull TicketHistory ticketHistory) {
        ticketRepositoryPort.findById(ticketHistory.getTicketId()).orElseThrow( () -> new TicketNotFoundException(ticketHistory.getTicketId()));
        return ticketHistoryRepositoryPort.createTicketHistory(ticketHistory);
    }

    private void saveTicketHistory(TicketHistory ticketHistory) {
        ticketHistoryRepositoryPort.createTicketHistory(ticketHistory);
    }

    private void saveTicketAssigment(TicketAssignment ticketAssignment) {
        ticketAssignmentRepositoryPort.saveTicketAssignment(ticketAssignment);
    }

    @Override
    public Ticket unassignedTicket(UUID ticketId) {
        Ticket ticket = ticketRepositoryPort.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
        if (ticket.getAssignedTo() != null) {
            ticket.setAssignedAt(null);
            ticket.setAssignedTo(null);
        }

        if (!ticketAssignmentRepositoryPort.existTicketAssignmentForTicketId(ticket.getId())) {
            throw new TicketAssignmentNotFoundException(ticket.getId());
        }
        TicketAssignment ticketAssignment = ticketAssignmentRepositoryPort.getTicketAssignmentForTicketId(ticketId).orElseThrow(()-> new TicketAssignmentNotFoundException(ticketId));
        ticketAssignment.setAssignedTo(null);
        ticketAssignment.setAssignedAt(null);
        ticketAssignment.setUnassignedAt(LocalDateTime.now());
        ticketAssignmentRepositoryPort.saveTicketAssignment(ticketAssignment);
        return ticketRepositoryPort.saveTicket(ticket);
    }
}
