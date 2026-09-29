package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    boolean existsByName(RoleEnum name);
    Optional<RoleEntity> findByName(String name);
}
