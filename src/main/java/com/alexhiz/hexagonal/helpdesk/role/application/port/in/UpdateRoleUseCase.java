package com.alexhiz.hexagonal.helpdesk.role.application.port.in;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;

import java.util.UUID;

public interface UpdateRoleUseCase {
    Role updateRole(UUID id, Role role);
}
