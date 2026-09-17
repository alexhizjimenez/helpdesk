package com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.in.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.CreateSlaUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.DeleteSlaUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.GetSlaByIdUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.ListSlasUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.UpdateSlaUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.in.rest.dto.SlaRequest;
import com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.in.rest.dto.SlaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/sla")
@RequiredArgsConstructor
@Tag(name = "SLA", description = "Operaciones para gestionar las políticas de SLA")
public class SlaController {
    private final CreateSlaUseCase createSlaUseCase;
    private final ListSlasUseCase listSlasUseCase;
    private final GetSlaByIdUseCase getSlaByIdUseCase;
    private final DeleteSlaUseCase deleteSlaUseCase;
    private final UpdateSlaUseCase updateSlaUseCase;

    @PostMapping
    @Operation(summary = "Crea una política SLA", description = "Crea una nueva política de SLA")
    @ApiResponse(responseCode = "201", description = "SLA creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos del SLA inválidos")
    @ApiResponse(responseCode = "409", description = "Ya existe una política SLA con el mismo nombre")
    public ResponseEntity<SlaResponse> create(@Valid @RequestBody SlaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SlaResponse.from(createSlaUseCase.create(request.toDomain())));
    }

    @GetMapping
    @Operation(summary = "Lista las políticas SLA", description = "Obtiene todas las políticas de SLA registradas")
    @ApiResponse(responseCode = "200", description = "Políticas SLA obtenidas correctamente")
    public ResponseEntity<List<SlaResponse>> getAll() {
        List<SlaResponse> slas = listSlasUseCase.getAllSlas().stream().map(SlaResponse::from).toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(slas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una política SLA", description = "Busca una política de SLA por su identificador")
    @ApiResponse(responseCode = "200", description = "Política SLA obtenida correctamente")
    @ApiResponse(responseCode = "404", description = "Política SLA no encontrada")
    public ResponseEntity<SlaResponse> getById(
            @Parameter(description = "Identificador único del SLA") @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(SlaResponse.from(getSlaByIdUseCase.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una política SLA", description = "Actualiza los datos de una política de SLA existente")
    @ApiResponse(responseCode = "200", description = "Política SLA actualizada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos del SLA inválidos")
    @ApiResponse(responseCode = "404", description = "Política SLA no encontrada")
    @ApiResponse(responseCode = "409", description = "Ya existe una política SLA con el mismo nombre")
    public ResponseEntity<SlaResponse> update(
            @Parameter(description = "Identificador único del SLA") @PathVariable UUID id,
            @Valid @RequestBody SlaRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(SlaResponse.from(updateSlaUseCase.update(id, request.toDomain())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una política SLA", description = "Elimina una política de SLA por su identificador")
    @ApiResponse(responseCode = "204", description = "Política SLA eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "Política SLA no encontrada")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador único del SLA") @PathVariable UUID id) {
        deleteSlaUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
