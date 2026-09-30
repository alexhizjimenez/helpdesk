package com.alexhiz.hexagonal.helpdesk.ticket.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketAttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private UserEntity uploadedby;

    @Column(nullable = false, columnDefinition = "TEXT", name = "file_name")
    private String fileName;

    @Column(nullable = false, columnDefinition = "TEXT", name = "file_path")
    private String filePath;

    @Column(nullable = false, columnDefinition = "TEXT", name = "content_type")
    private String contentType;

    @Column(nullable = false, columnDefinition = "TEXT", name = "file_size")
    private String fileSize;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        if(this.updatedAt == null){
            this.updatedAt = now;
        }
    }

    @PreUpdate
    public  void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
