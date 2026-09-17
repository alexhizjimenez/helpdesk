package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.role.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.role.domain.exception.RoleNotFoundException;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto.RoleRequest;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto.RoleResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Operaciones para gestionar los roles")
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final ListRoleUseCase listRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;

    @PostMapping
    @Operation(summary = "Crea un rol", description = "Crea un nuevo rol")
    @ApiResponse(responseCode = "201", description = "Rol creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos del rol inválidos")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        Role saved = createRoleUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(RoleResponse.from(saved));
    }

    @GetMapping
    @Operation(summary = "Lista los roles", description = "Obtiene todos los roles registrados")
    @ApiResponse(responseCode = "200", description = "Roles obtenidos correctamente")
    public ResponseEntity<List<RoleResponse>> getRoles(){
        List<RoleResponse> responses = listRoleUseCase.listRole().stream().map(RoleResponse::from).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un rol", description = "Busca un rol por su identificador")
    @ApiResponse(responseCode = "200", description = "Rol obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<RoleResponse> getRole(@PathVariable UUID id){
        Role role = getRoleByIdUseCase.getRoleById(id).orElseThrow(() -> new RoleNotFoundException(id));
        return ResponseEntity.ok(RoleResponse.from(role));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un rol", description = "Actualiza los datos de un rol existente")
    @ApiResponse(responseCode = "202", description = "Rol actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request){
        Role updateRole = updateRoleUseCase.updateRole(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(RoleResponse.from(updateRole));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un rol", description = "Elimina un rol por su identificador")
    @ApiResponse(responseCode = "204", description = "Rol eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id){
        deleteRoleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
