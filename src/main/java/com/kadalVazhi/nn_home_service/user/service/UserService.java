package com.kadalVazhi.nn_home_service.user.service;

import com.kadalVazhi.nn_home_service.common.exception.DuplicateResourceException;
import com.kadalVazhi.nn_home_service.common.exception.ResourceNotFoundException;
import com.kadalVazhi.nn_home_service.user.domain.User;
import com.kadalVazhi.nn_home_service.user.domain.UserProfile;
import com.kadalVazhi.nn_home_service.user.dto.RegisterUserRequest;
import com.kadalVazhi.nn_home_service.user.dto.UpdateProfileRequest;
import com.kadalVazhi.nn_home_service.user.dto.UserProfileResponse;
import com.kadalVazhi.nn_home_service.user.repository.UserFollowRepository;
import com.kadalVazhi.nn_home_service.user.repository.UserProfileRepository;
import com.kadalVazhi.nn_home_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service managing user identity, registration, and maritime profile updates.
 *
 * WHY @Transactional:
 * Registration creates both a User row and a UserProfile row.
 * If creating UserProfile fails, the User creation is automatically rolled back,
 * guaranteeing zero orphaned database records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserFollowRepository userFollowRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProfileResponse registerUser(RegisterUserRequest request) {
        log.info("Attempting to register user with phone: {}", request.getPhoneNumber());

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number is already registered: " + request.getPhoneNumber());
        }

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .preferredLanguage(request.getPreferredLanguage())
                .isVerified(false)
                .isActive(true)
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .age(request.getAge())
                .nativePlace(request.getNativePlace())
                .address(request.getAddress())
                .experienceYears(request.getExperienceYears())
                .primaryHarbor(request.getPrimaryHarbor())
                .build();

        user.setProfile(profile);

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user with ID: {}", savedUser.getId());

        return mapToResponse(savedUser, savedUser.getProfile(), 0, 0);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + userId));

        long followersCount = userFollowRepository.countByFollowingId(userId);
        long followingCount = userFollowRepository.countByFollowerId(userId);

        return mapToResponse(profile.getUser(), profile, followersCount, followingCount);
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + userId));

        User user = profile.getUser();

        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getAge() != null) profile.setAge(request.getAge());
        if (request.getNativePlace() != null) profile.setNativePlace(request.getNativePlace());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getExperienceYears() != null) profile.setExperienceYears(request.getExperienceYears());
        if (request.getPrimaryHarbor() != null) profile.setPrimaryHarbor(request.getPrimaryHarbor());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getHeaderBannerUrl() != null) profile.setHeaderBannerUrl(request.getHeaderBannerUrl());
        if (request.getBio() != null) profile.setBio(request.getBio());

        if (request.getPreferredLanguage() != null) {
            user.setPreferredLanguage(request.getPreferredLanguage());
        }

        UserProfile updatedProfile = userProfileRepository.save(profile);
        long followersCount = userFollowRepository.countByFollowingId(userId);
        long followingCount = userFollowRepository.countByFollowerId(userId);

        return mapToResponse(user, updatedProfile, followersCount, followingCount);
    }

    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getProfilesByHarbor(String harbor, Pageable pageable) {
        return userProfileRepository.findByPrimaryHarbor(harbor, pageable)
                .map(profile -> {
                    long followers = userFollowRepository.countByFollowingId(profile.getUser().getId());
                    long following = userFollowRepository.countByFollowerId(profile.getUser().getId());
                    return mapToResponse(profile.getUser(), profile, followers, following);
                });
    }

    private UserProfileResponse mapToResponse(User user, UserProfile profile, long followers, long following) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .preferredLanguage(user.getPreferredLanguage())
                .isVerified(user.isVerified())
                .fullName(profile.getFullName())
                .age(profile.getAge())
                .nativePlace(profile.getNativePlace())
                .address(profile.getAddress())
                .experienceYears(profile.getExperienceYears())
                .primaryHarbor(profile.getPrimaryHarbor())
                .avatarUrl(profile.getAvatarUrl())
                .headerBannerUrl(profile.getHeaderBannerUrl())
                .bio(profile.getBio())
                .followersCount(followers)
                .followingCount(following)
                .memberSince(user.getCreatedAt())
                .build();
    }
}
