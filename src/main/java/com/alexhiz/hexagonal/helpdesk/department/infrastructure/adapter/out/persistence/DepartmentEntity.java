package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "departments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false)
    private UUID publicId;

    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }

        if (this.publicId == null) {
            this.publicId = UUID.randomUUID();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
