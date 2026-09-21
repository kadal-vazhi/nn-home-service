package com.kadalVazhi.nn_home_service.user.service;

import com.kadalVazhi.nn_home_service.common.exception.BadRequestException;
import com.kadalVazhi.nn_home_service.common.exception.DuplicateResourceException;
import com.kadalVazhi.nn_home_service.common.exception.ResourceNotFoundException;
import com.kadalVazhi.nn_home_service.user.domain.User;
import com.kadalVazhi.nn_home_service.user.domain.UserFollow;
import com.kadalVazhi.nn_home_service.user.domain.UserProfile;
import com.kadalVazhi.nn_home_service.user.dto.FollowActionResponse;
import com.kadalVazhi.nn_home_service.user.dto.UserProfileResponse;
import com.kadalVazhi.nn_home_service.user.repository.UserFollowRepository;
import com.kadalVazhi.nn_home_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service managing social following graph (e.g. buyers following fishermen, crew following captains).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    @Transactional
    public FollowActionResponse follow(UUID followerId, UUID followingId) {
        if (followerId.equals(followingId)) {
            throw new BadRequestException("Users cannot follow themselves.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Follower user not found: " + followerId));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user to follow not found: " + followingId));

        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new DuplicateResourceException("You are already following this user.");
        }

        UserFollow followRelation = UserFollow.builder()
                .follower(follower)
                .following(following)
                .build();

        userFollowRepository.save(followRelation);
        long totalFollowers = userFollowRepository.countByFollowingId(followingId);

        log.info("User {} followed user {}", followerId, followingId);

        return FollowActionResponse.builder()
                .followerId(followerId)
                .followingId(followingId)
                .status("FOLLOWED")
                .totalFollowers(totalFollowers)
                .build();
    }

    @Transactional
    public FollowActionResponse unfollow(UUID followerId, UUID followingId) {
        if (!userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BadRequestException("You are not following this user.");
        }

        userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        long totalFollowers = userFollowRepository.countByFollowingId(followingId);

        log.info("User {} unfollowed user {}", followerId, followingId);

        return FollowActionResponse.builder()
                .followerId(followerId)
                .followingId(followingId)
                .status("UNFOLLOWED")
                .totalFollowers(totalFollowers)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getFollowers(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }

        return userFollowRepository.findByFollowingId(userId, pageable)
                .map(relation -> {
                    User follower = relation.getFollower();
                    UserProfile profile = follower.getProfile();
                    long followersCount = userFollowRepository.countByFollowingId(follower.getId());
                    long followingCount = userFollowRepository.countByFollowerId(follower.getId());
                    return mapToResponse(follower, profile, followersCount, followingCount);
                });
    }

    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getFollowing(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }

        return userFollowRepository.findByFollowerId(userId, pageable)
                .map(relation -> {
                    User following = relation.getFollowing();
                    UserProfile profile = following.getProfile();
                    long followersCount = userFollowRepository.countByFollowingId(following.getId());
                    long followingCount = userFollowRepository.countByFollowerId(following.getId());
                    return mapToResponse(following, profile, followersCount, followingCount);
                });
    }

    private UserProfileResponse mapToResponse(User user, UserProfile profile, long followers, long following) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .preferredLanguage(user.getPreferredLanguage())
                .isVerified(user.isVerified())
                .fullName(profile != null ? profile.getFullName() : null)
                .age(profile != null ? profile.getAge() : null)
                .nativePlace(profile != null ? profile.getNativePlace() : null)
                .address(profile != null ? profile.getAddress() : null)
                .experienceYears(profile != null ? profile.getExperienceYears() : null)
                .primaryHarbor(profile != null ? profile.getPrimaryHarbor() : null)
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .headerBannerUrl(profile != null ? profile.getHeaderBannerUrl() : null)
                .bio(profile != null ? profile.getBio() : null)
                .followersCount(followers)
                .followingCount(following)
                .memberSince(user.getCreatedAt())
                .build();
    }
}
