package com.alexhiz.hexagonal.helpdesk.role.application.port.in;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;

import java.util.Optional;
import java.util.UUID;

public interface GetRoleByIdUseCase {
    Optional<Role> getRoleById(UUID id);

}
