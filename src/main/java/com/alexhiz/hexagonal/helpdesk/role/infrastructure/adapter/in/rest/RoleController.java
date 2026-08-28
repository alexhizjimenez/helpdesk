package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.role.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.role.domain.exception.RoleNotFoundException;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto.RoleRequest;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto.RoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final ListRoleUseCase listRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;

    @PostMapping
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        Role saved = createRoleUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(RoleResponse.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getRoles(){
        List<RoleResponse> responses = listRoleUseCase.listRole().stream().map(RoleResponse::from).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable UUID id){
        Role role = getRoleByIdUseCase.getRoleById(id).orElseThrow(() -> new RoleNotFoundException(id));
        return ResponseEntity.ok(RoleResponse.from(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request){
        Role updateRole = updateRoleUseCase.updateRole(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(RoleResponse.from(updateRole));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id){
        deleteRoleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
