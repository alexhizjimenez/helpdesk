package com.alexhiz.hexagonal.helpdesk.role.application.port.in;

import java.util.UUID;

public interface DeleteRoleUseCase {
    void delete(UUID id);
}
