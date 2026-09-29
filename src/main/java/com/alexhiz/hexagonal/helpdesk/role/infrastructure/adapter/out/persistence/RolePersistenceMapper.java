package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolePersistenceMapper {
    private final PermissionPersistenceMapper permissionPersistenceMapper;
    public Role toDomain(RoleEntity roleEntity) {
        if (roleEntity == null) return null;
        return Role.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .createdAt(roleEntity.getCreatedAt())
                .updatedAt(roleEntity.getUpdatedAt())
                .permissions( roleEntity.getPermissions()
                        .stream()
                        .map(permissionPersistenceMapper::toDomain)
                        .collect(Collectors.toSet()))
                .build();
    }

    public RoleEntity toEntity(Role role) {
        if (role == null) return null;
        return RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .permissions(
                        role.getPermissions()
                                .stream()
                                .map(permissionPersistenceMapper::toEntity)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
