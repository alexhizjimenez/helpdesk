package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.category.application.port.in.GetCategoryByIdUseCase;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.department.application.port.in.GetDepartmentByIdUseCase;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.AssignTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.ChangeStatusTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.CreateTicketUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.application.port.in.PageTicketsUseCase;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.Ticket;
import com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest.dto.AssignTicketRequest;
import com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest.dto.TicketRequest;
import com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.in.rest.dto.TicketResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Operaciones para gestionar los tickets")
public class TicketController {

    private final CreateTicketUseCase createTicketUseCase;
    private final PageTicketsUseCase pageTicketsUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final GetDepartmentByIdUseCase getDepartmentByIdUseCase;
    private final AssignTicketUseCase assignTicketUseCase;
    private final ChangeStatusTicketUseCase changeStatusTicketUseCase;

    @PostMapping
    @Operation(summary = "Crea un ticket", description = "Crea un nuevo ticket")
    @ApiResponse(responseCode = "200", description = "Ticket creado correctamente")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request){
        Ticket ticket = createTicketUseCase.createTicket(request.toDomain());

        return ResponseEntity.status(HttpStatus.OK).body(toResponse(ticket));
    }

    @GetMapping("/pages")
    @Operation(summary = "Obtiene tickets paginados", description = "Obtiene una página de tickets usando los parámetros de paginación")
    @ApiResponse(responseCode = "200", description = "Página de tickets obtenida correctamente")
    public ResponseEntity<PageResult<TicketResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        PageResult<TicketResponse> domainPage = pageTicketsUseCase.execute(new PageQuery(page, size)).map(TicketResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(domainPage);
    }

    @PatchMapping("/{ticketId}/assign")
    private ResponseEntity<TicketResponse>  assign(@PathVariable UUID ticketId, @Valid @RequestBody AssignTicketRequest request){
        Ticket ticket = assignTicketUseCase.assignTicket(ticketId, request.assingId());
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(ticket));
    }

    @PatchMapping("/{ticketId}/resolved")
    private ResponseEntity<TicketResponse>  resolved(@PathVariable UUID ticketId){
        Ticket ticket = changeStatusTicketUseCase.changeStatusTicket(ticketId, "resolved");
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(ticket));
    }

    @PatchMapping("/{ticketId}/closed")
    private ResponseEntity<TicketResponse>  closed(@PathVariable UUID ticketId){
        Ticket ticket = changeStatusTicketUseCase.changeStatusTicket(ticketId, "closed");
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(ticket));
    }

    @PatchMapping("/{ticketId}/reopen")
    private ResponseEntity<TicketResponse>  reopen(@PathVariable UUID ticketId){
        Ticket ticket = changeStatusTicketUseCase.changeStatusTicket(ticketId, "reopen");
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(ticket));
    }


    private TicketResponse toResponse(Ticket ticket) {
        String categoryName = null;
        if (ticket.getCategoryId() != null) {
            Category category = getCategoryByIdUseCase.getCategoryById(ticket.getCategoryId());
            if (category != null) {
                categoryName = category.getName();
            }
        }

        String departmentName = null;
        if (ticket.getDepartmentId() != null) {
            Department department = getDepartmentByIdUseCase.getDepartmentById(ticket.getDepartmentId());
            if (department != null) {
                departmentName = department.getName();
            }
        }

        return TicketResponse.from(ticket, categoryName, departmentName);
    }

}
