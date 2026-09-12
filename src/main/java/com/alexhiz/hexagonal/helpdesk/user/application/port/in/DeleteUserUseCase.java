package com.alexhiz.hexagonal.helpdesk.user.application.port.in;

import java.util.UUID;

public interface DeleteUserUseCase {
    void delete(UUID id);
}
