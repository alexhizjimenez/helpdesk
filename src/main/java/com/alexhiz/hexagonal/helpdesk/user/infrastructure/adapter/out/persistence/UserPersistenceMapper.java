package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence.DepartmentEntity;
import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.RolePersistenceMapper;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserPersistenceMapper {

    private final RolePersistenceMapper rolePersistenceMapper;
    private final EntityManager entityManager;

    public User toDomain(UserEntity userEntity) {
        if (userEntity == null) return null;
        return User.builder()
                .id(userEntity.getId())
                .fullName(userEntity.getFullName())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .phone(userEntity.getPhone())
                .departmentId(userEntity.getDepartment() != null ? userEntity.getDepartment().getPublicId() : null)
                .isAccountNonExpired(userEntity.isAccountNonExpired())
                .isAccountNonLocked(userEntity.isAccountNonLocked())
                .isCredentialsNonExpired(userEntity.isCredentialsNonExpired())
                .isEnabled(userEntity.isEnabled())
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
        DepartmentEntity departmentEntity = null;
        if (user.getDepartmentId() != null) {
            departmentEntity = entityManager.getReference(DepartmentEntity.class, user.getDepartmentId());
        }
        return UserEntity.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .phone(user.getPhone())
                .department(departmentEntity)
                .isAccountNonExpired(user.isAccountNonExpired())
                .isAccountNonLocked(user.isAccountNonLocked())
                .isCredentialsNonExpired(user.isCredentialsNonExpired())
                .isEnabled(user.isEnabled())
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
