package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
        if(this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
