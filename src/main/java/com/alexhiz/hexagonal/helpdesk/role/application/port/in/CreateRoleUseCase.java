package com.alexhiz.hexagonal.helpdesk.role.application.port.in;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;

public interface CreateRoleUseCase {
    Role create(Role role);
}
