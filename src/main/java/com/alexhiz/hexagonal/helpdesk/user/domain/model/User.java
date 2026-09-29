package com.alexhiz.hexagonal.helpdesk.user.domain.model;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @EqualsAndHashCode.Include
    private UUID id;
    private String fullName;
    private String email;
    private String username;
    private String password;
    private String phone;
    private UUID departmentId;
    @Builder.Default
    private boolean isEnabled = true;
    @Builder.Default
    private boolean isAccountNonExpired = true;
    @Builder.Default
    private boolean isAccountNonLocked = true;
    @Builder.Default
    private boolean isCredentialsNonExpired = true;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
