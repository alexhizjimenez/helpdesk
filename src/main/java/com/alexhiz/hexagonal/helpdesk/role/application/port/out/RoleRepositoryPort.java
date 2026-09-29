package com.alexhiz.hexagonal.helpdesk.role.application.port.out;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.RoleEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepositoryPort {
    Role save(Role role);
    Optional<Role> findById(UUID id);
    boolean existsById(UUID id);
    boolean existsByName(RoleEnum name);
    List<Role> findAll();
    void delete(UUID id);
}
