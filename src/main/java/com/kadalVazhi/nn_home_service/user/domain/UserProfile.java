package com.kadalVazhi.nn_home_service.user.domain;

import com.kadalVazhi.nn_home_service.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Detailed maritime profile for a fisherman, captain, boat owner, or customer.
 *
 * WHY THIS DESIGN:
 * 1. user_id has a UNIQUE constraint ensuring strict 1:1 relationship with User.
 * 2. primary_harbor is indexed so harbor-based crew matching and catch pre-orders are fast.
 * 3. Keeps User table lightweight so high-frequency auth tokens and lookups stay in fast DB cache.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "user_profiles",
    indexes = {
        @Index(name = "idx_profile_harbor", columnList = "primary_harbor"),
        @Index(name = "idx_profile_native", columnList = "native_place")
    }
)
public class UserProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "native_place", length = 100)
    private String nativePlace;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "primary_harbor", length = 100)
    private String primaryHarbor;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(name = "header_banner_url", length = 512)
    private String headerBannerUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
}
