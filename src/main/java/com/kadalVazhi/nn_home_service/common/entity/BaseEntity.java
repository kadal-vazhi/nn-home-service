package com.kadalVazhi.nn_home_service.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BaseEntity provides standard audit fields and a globally unique UUID primary key.
 *
 * WHY THIS DESIGN:
 * 1. @MappedSuperclass: Prevents code duplication by inheriting ID and audit fields across all entities.
 * 2. UUID PK: Prevents sequential ID enumeration attacks in distributed microservices.
 * 3. @Version (Optimistic Locking): Prevents race conditions when concurrent requests update the same row.
 * 4. AuditingEntityListener: Automatically sets createdAt and updatedAt without manual setters.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;
}
