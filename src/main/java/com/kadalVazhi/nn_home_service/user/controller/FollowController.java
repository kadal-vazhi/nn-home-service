package com.kadalVazhi.nn_home_service.user.controller;

import com.kadalVazhi.nn_home_service.common.dto.ApiResponse;
import com.kadalVazhi.nn_home_service.user.dto.FollowActionResponse;
import com.kadalVazhi.nn_home_service.user.dto.UserProfileResponse;
import com.kadalVazhi.nn_home_service.user.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/follow")
@RequiredArgsConstructor
@Tag(name = "Social Following", description = "Follow/unfollow fishermen and list network connections")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetUserId}")
    @Operation(summary = "Follow a user or boat owner", description = "Establishes a follow relationship and increments follower count")
    public ResponseEntity<ApiResponse<FollowActionResponse>> followUser(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId) {
        FollowActionResponse response = followService.follow(userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Followed successfully"));
    }

    @DeleteMapping("/{targetUserId}")
    @Operation(summary = "Unfollow a user", description = "Removes a follow relationship and decrements follower count")
    public ResponseEntity<ApiResponse<FollowActionResponse>> unfollowUser(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId) {
        FollowActionResponse response = followService.unfollow(userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Unfollowed successfully"));
    }

    @GetMapping("/followers")
    @Operation(summary = "List followers", description = "Retrieves a paginated list of users following this fisherman/boat")
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> getFollowers(
            @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserProfileResponse> followers = followService.getFollowers(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(followers));
    }

    @GetMapping("/following")
    @Operation(summary = "List following", description = "Retrieves a paginated list of users this fisherman is following")
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> getFollowing(
            @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserProfileResponse> following = followService.getFollowing(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(following));
    }
}
