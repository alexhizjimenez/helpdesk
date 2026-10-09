package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.SlaPriotity;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketPriority;
import com.alexhiz.hexagonal.helpdesk.ticket.domain.model.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name="tickets")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class TicketEntity {

    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column (nullable = false, unique = true)
    @NotBlank(message = "Ticket number is required")
    private String ticketNumber;

    @Column (nullable = false, length = 255)
    @NotBlank(message = "title  is required")
    @Size (min = 3, max = 255)
    private String title;

    @Column (nullable = true, length = 255)
    @Size (min = 3, max = 255)
    private String description;

    @Column (nullable = false, length = 255)
    @NotNull
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column (nullable = false, length = 255)
    @NotNull
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    private UUID categoryId;

    private UUID departmentId;

    private UUID createdBy;

    private UUID assignedTo;

    private UUID slaPolicyId;

    @CreationTimestamp
    @Column (name = "opened_at", nullable = false, updatable = false)
    private LocalDateTime openedAt;

    @CreationTimestamp
    @Column (name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column (name = "resolved_at", nullable = true)
    private LocalDateTime resolvedAt;

    @Column (name = "closed_at", nullable = true)
    private LocalDateTime closedAt;

    @Column (name = "due_at", nullable = true)
    private LocalDateTime dueAt;

    @CreationTimestamp 
    @Column (name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp 
    @Column (name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist 
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        if(this.updatedAt == null){
            this.updatedAt = now;
        }

        if(this.openedAt == null){
            this.openedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

}
