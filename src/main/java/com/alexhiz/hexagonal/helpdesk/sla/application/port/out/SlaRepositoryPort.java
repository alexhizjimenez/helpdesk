package com.alexhiz.hexagonal.helpdesk.sla.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

public interface SlaRepositoryPort {
    Sla save(Sla sla);

    void delete(UUID id);

    Optional<Sla> getById(UUID id);

    List<Sla> findAll();

    boolean existsById(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
