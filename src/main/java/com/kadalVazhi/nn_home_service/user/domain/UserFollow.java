package com.kadalVazhi.nn_home_service.user.domain;

import com.kadalVazhi.nn_home_service.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Social relationship entity (Follow / Connect) for fishermen, boats, and buyers.
 *
 * WHY THIS DESIGN:
 * 1. Composite UNIQUE constraint (follower_id, following_id): Strictly prevents duplicate follows at the DB level.
 * 2. Bi-directional indexes:
 *    - idx_follow_follower: Speeds up "Who am I following?" query.
 *    - idx_follow_following: Speeds up "Who are my followers?" query.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "user_follows",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_follower_following",
            columnNames = {"follower_id", "following_id"}
        )
    },
    indexes = {
        @Index(name = "idx_follow_follower", columnList = "follower_id"),
        @Index(name = "idx_follow_following", columnList = "following_id")
    }
)
public class UserFollow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;
}
