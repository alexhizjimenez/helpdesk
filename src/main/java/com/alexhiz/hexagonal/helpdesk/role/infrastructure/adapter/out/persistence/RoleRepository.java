package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    boolean existsByName(String name);
    Optional<RoleEntity> findByName(String name);
}
