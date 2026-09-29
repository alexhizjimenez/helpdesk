package com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Permission;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.PermissionEnum;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.RoleEnum;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PermissionResponse(
        UUID id,
        PermissionEnum name
) {
    public static PermissionResponse from(Permission permission){
        return PermissionResponse.builder()
                .id(permission.getId()).name(PermissionEnum.valueOf(permission.getName().toString())).build();
    }
}
