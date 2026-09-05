package com.innercircle.controller;

import com.innercircle.dto.follow.FollowResponse;
import com.innercircle.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<FollowResponse> follow(
            Authentication authentication,
            @PathVariable UUID userId) {
        UUID viewerId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(followService.follow(viewerId, userId));
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<FollowResponse> unfollow(
            Authentication authentication,
            @PathVariable UUID userId) {
        UUID viewerId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(followService.unfollow(viewerId, userId));
    }

    @GetMapping("/{userId}/follow")
    public ResponseEntity<FollowResponse> status(
            Authentication authentication,
            @PathVariable UUID userId) {
        UUID viewerId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(followService.status(viewerId, userId));
    }
}
