package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RolePersistenceMapper {
    public Role toDomain(RoleEntity roleEntity) {
        if (roleEntity == null) return null;
        return Role.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .createdAt(roleEntity.getCreatedAt())
                .updatedAt(roleEntity.getUpdatedAt())
                .build();
    }

    public RoleEntity toEntity(Role role) {
        if (role == null) return null;
        return RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
