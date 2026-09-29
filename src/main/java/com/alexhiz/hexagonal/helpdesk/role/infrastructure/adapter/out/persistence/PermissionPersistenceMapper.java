package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Permission;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.PermissionEnum;
import org.springframework.stereotype.Component;


@Component
public class PermissionPersistenceMapper {
    public Permission toDomain(PermissionEntity permissionEntity) {
        if (permissionEntity == null) return null;
        return Permission.builder()
                .id(permissionEntity.getId())
                .name(PermissionEnum.valueOf(permissionEntity.getName().toString()))
                .createdAt(permissionEntity.getCreatedAt())
                .updatedAt(permissionEntity.getUpdatedAt())
                .build();
    }

    public PermissionEntity toEntity(Permission permission) {
        if (permission == null) return null;
        return PermissionEntity.builder()
                .id(permission.getId())
                .name(permission.getName())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}
