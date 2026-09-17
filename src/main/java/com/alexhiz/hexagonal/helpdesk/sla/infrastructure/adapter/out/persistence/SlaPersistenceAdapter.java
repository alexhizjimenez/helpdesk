package com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.alexhiz.hexagonal.helpdesk.sla.application.port.out.SlaRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SlaPersistenceAdapter implements SlaRepositoryPort {
    private final SlaRepository slaRepository;
    private final SlaPersistenceMapper slaPersistenceMapper;

    @Override
    public Sla save(Sla sla) {
        return slaPersistenceMapper.toDomain(slaRepository.save(slaPersistenceMapper.toEntity(sla)));
    }

    @Override
    public void delete(UUID id) {
        slaRepository.deleteById(id);
    }

    @Override
    public Optional<Sla> getById(UUID id) {
        return slaRepository.findById(id).map(slaPersistenceMapper::toDomain);
    }

    @Override
    public List<Sla> findAll() {
        return slaRepository.findAll().stream().map(slaPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return slaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return slaRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return slaRepository.existsByNameAndIdNot(name, id);
    }

}
