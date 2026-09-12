package com.alexhiz.hexagonal.helpdesk.user.application.port.in;

import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;

public interface UpdateUserUseCase {
    User update(UUID id, User user);
}
