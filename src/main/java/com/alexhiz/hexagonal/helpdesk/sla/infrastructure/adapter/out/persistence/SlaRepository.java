package com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SlaRepository extends JpaRepository<SlaEntity, UUID> {
    boolean existsById(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
