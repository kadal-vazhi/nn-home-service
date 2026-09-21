package com.kadalVazhi.nn_home_service.user.domain;

import com.kadalVazhi.nn_home_service.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Authentication and identity core entity.
 *
 * WHY THIS DESIGN:
 * 1. Phone number is the primary unique identifier (fishermen primarily use mobile numbers).
 * 2. Role is stored as STRING in the DB (prevents enum ordinal index reordering bugs).
 * 3. Lazy OneToOne to UserProfile ensures profile is loaded only when requested, saving DB bandwidth.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_phone", columnList = "phone_number", unique = true),
        @Index(name = "idx_users_role", columnList = "role")
    }
)
public class User extends BaseEntity {

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 10)
    @Builder.Default
    private Language preferredLanguage = Language.TA;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean isVerified = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserProfile profile;
}
