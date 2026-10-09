package com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.SlaPriotity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sla_policies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 150)
    private SlaPriotity priority;

    @Column(name = "response_time_minutes", nullable = false)
    private String responseTimeMinutes;

    @Column(name = "resolution_time_minutes", nullable = false)
    private String resolutionTimeMinutes;

    @Column(name = "active", nullable = false)
    private String active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = "true";
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
