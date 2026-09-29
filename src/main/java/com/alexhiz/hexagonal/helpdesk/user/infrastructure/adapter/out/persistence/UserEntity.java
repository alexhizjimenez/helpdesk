package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.role.infrastructure.adapter.out.persistence.RoleEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "is_account_non_expired", nullable = false, columnDefinition = "boolean default true")
    private boolean isAccountNonExpired = true;

    @Column(name = "is_account_non_locked", nullable = false, columnDefinition = "boolean default true")
    private boolean isAccountNonLocked = true;

    @Column(name = "is_credentials_non_expired", nullable = false, columnDefinition = "boolean default true")
    private boolean isCredentialsNonExpired = true;

    @Column(name = "is_enabled", nullable = false, columnDefinition = "boolean default true")
    private boolean isEnabled = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
