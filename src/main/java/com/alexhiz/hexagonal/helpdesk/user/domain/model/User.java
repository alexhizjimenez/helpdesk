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
    private String password;
    private String phone;
    private UUID departmentId;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
