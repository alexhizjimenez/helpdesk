package com.alexhiz.hexagonal.helpdesk.auth.application.port.out;

import com.alexhiz.hexagonal.helpdesk.auth.domain.model.Auth;

import java.util.Optional;

public interface AuthRepositoryPort {
    Optional<Auth> findByUsername(String username);
}
