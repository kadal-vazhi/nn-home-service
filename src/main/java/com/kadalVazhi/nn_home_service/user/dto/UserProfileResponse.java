package com.kadalVazhi.nn_home_service.user.dto;

import com.kadalVazhi.nn_home_service.user.domain.Language;
import com.kadalVazhi.nn_home_service.user.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Public and authorized profile details returned to mobile/web clients.
 * Notice: passwordHash is NEVER exposed in this DTO!
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID userId;
    private String phoneNumber;
    private Role role;
    private Language preferredLanguage;
    private boolean isVerified;
    private String fullName;
    private Integer age;
    private String nativePlace;
    private String address;
    private Integer experienceYears;
    private String primaryHarbor;
    private String avatarUrl;
    private String headerBannerUrl;
    private String bio;
    private long followersCount;
    private long followingCount;
    private LocalDateTime memberSince;
}
