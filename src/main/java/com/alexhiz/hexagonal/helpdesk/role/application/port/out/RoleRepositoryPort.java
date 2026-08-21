package com.alexhiz.hexagonal.helpdesk.role.application.port.out;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepositoryPort {
    Role save(Role role);
    Optional<Role> findById(UUID id);
    boolean existsById(UUID id);
    boolean existsByName(String name);
}
