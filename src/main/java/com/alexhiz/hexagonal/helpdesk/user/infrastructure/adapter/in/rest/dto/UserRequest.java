package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest.dto;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank(message = "Username is required")
        @Size(min = 3, message = "Username must be at least 6 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        String phone,

        UUID departmentId,

        Set<UUID> roleIds
) {
    //no jala la autorizacion
    public User toDomain() {
        return User.builder()
                .fullName(fullName)
                .email(email)
                .username(username)
                .password(new BCryptPasswordEncoder().encode(password))
                .phone(phone)
                .departmentId(departmentId)
                .roles(roleIds != null
                        ? roleIds.stream()
                                .map(roleId -> Role.builder().id(roleId).build())
                                .collect(Collectors.toSet())
                        : null)
                .build();
    }
}

