package com.alexhiz.hexagonal.helpdesk.user.application.port.out;

import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsById(UUID id);
    boolean existsByEmail(String email);
}
