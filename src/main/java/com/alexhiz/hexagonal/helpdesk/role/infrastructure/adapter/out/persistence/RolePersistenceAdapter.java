package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.role.application.port.out.RoleRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RolePersistenceAdapter implements RoleRepositoryPort {
    private final RoleRepository roleRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    public RolePersistenceAdapter(RoleRepository roleRepository, RolePersistenceMapper rolePersistenceMapper) {
        this.roleRepository = roleRepository;
        this.rolePersistenceMapper = rolePersistenceMapper;
    }

    @Override
    public Role save(Role role) {
        RoleEntity roleEntity = rolePersistenceMapper.toEntity(role);
        RoleEntity saved = roleRepository.save(roleEntity);
        return rolePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleRepository.findById(id).map(rolePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return roleRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}
