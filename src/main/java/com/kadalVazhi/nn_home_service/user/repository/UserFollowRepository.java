package com.kadalVazhi.nn_home_service.user.repository;

import com.kadalVazhi.nn_home_service.user.domain.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    long countByFollowingId(UUID userId); // Total followers count

    long countByFollowerId(UUID userId);  // Total following count

    Page<UserFollow> findByFollowingId(UUID userId, Pageable pageable); // Page of followers

    Page<UserFollow> findByFollowerId(UUID userId, Pageable pageable);  // Page of following
}
