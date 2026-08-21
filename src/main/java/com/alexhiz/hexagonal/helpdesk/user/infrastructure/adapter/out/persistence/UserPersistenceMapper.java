package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.RolePersistenceMapper;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserPersistenceMapper {

    private final RolePersistenceMapper rolePersistenceMapper;

    public User toDomain(UserEntity userEntity) {
        if (userEntity == null) return null;
        return User.builder()
                .id(userEntity.getId())
                .fullName(userEntity.getFullName())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .phone(userEntity.getPhone())
                .departmentId(userEntity.getDepartmentId())
                .roles(userEntity.getRoles() != null
                        ? userEntity.getRoles().stream()
                                .map(rolePersistenceMapper::toDomain)
                                .collect(Collectors.toSet())
                        : null)
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;
        return UserEntity.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phone(user.getPhone())
                .departmentId(user.getDepartmentId())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream()
                                .map(rolePersistenceMapper::toEntity)
                                .collect(Collectors.toSet())
                        : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
