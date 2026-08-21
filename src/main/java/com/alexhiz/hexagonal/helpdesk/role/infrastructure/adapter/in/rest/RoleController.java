package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.role.application.port.in.CreateRoleUseCase;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto.RoleRequest;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto.RoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;

    @PostMapping("/")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        Role role = Role.builder()
                .name(request.name())
                .build();
        Role saved = createRoleUseCase.create(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoleResponse.from(saved));
    }
}
