package com.kadalVazhi.nn_home_service.user.controller;

import com.kadalVazhi.nn_home_service.common.dto.ApiResponse;
import com.kadalVazhi.nn_home_service.user.dto.RegisterUserRequest;
import com.kadalVazhi.nn_home_service.user.dto.UpdateProfileRequest;
import com.kadalVazhi.nn_home_service.user.dto.UserProfileResponse;
import com.kadalVazhi.nn_home_service.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User & Profile Management", description = "User registration, profile management, and harbor search")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Onboards a fisherman, captain, boat owner, crew, or buyer with profile details")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
        UserProfileResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "User registered successfully"));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by ID", description = "Retrieves complete maritime profile, experience, and follower stats")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@PathVariable UUID userId) {
        UserProfileResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile", description = "Updates harbor, experience, bio, avatar, or language preference")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Profile updated successfully"));
    }

    @GetMapping("/harbor/{harborName}")
    @Operation(summary = "Find users by harbor", description = "Filters fishermen and captains docked at a specific fishing harbor")
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> getByHarbor(
            @PathVariable String harborName,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserProfileResponse> profiles = userService.getProfilesByHarbor(harborName, pageable);
        return ResponseEntity.ok(ApiResponse.ok(profiles));
    }
}
