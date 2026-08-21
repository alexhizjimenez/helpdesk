package com.alexhiz.hexagonal.helpdesk.user.application.port.in;

import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;

public interface CreateUserUseCase {
    User create(User user);
}
