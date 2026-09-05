package com.innercircle.controller;

import com.innercircle.dto.follow.ConnectionResponse;
import com.innercircle.dto.follow.FollowResponse;
import com.innercircle.service.FollowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class FollowController {
    private final FollowService followService;
    public FollowController(FollowService followService){this.followService=followService;}
    @PostMapping("/{userId}/follow") public ResponseEntity<FollowResponse> follow(Authentication a,@PathVariable UUID userId){return ResponseEntity.ok(followService.follow(UUID.fromString(a.getName()),userId));}
    @DeleteMapping("/{userId}/follow") public ResponseEntity<FollowResponse> unfollow(Authentication a,@PathVariable UUID userId){return ResponseEntity.ok(followService.unfollow(UUID.fromString(a.getName()),userId));}
    @GetMapping("/{userId}/follow") public ResponseEntity<FollowResponse> status(Authentication a,@PathVariable UUID userId){return ResponseEntity.ok(followService.status(UUID.fromString(a.getName()),userId));}
    @GetMapping("/me/connections") public ResponseEntity<Page<ConnectionResponse>> connections(Authentication a,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){int safePage=Math.max(page,0),safeSize=Math.min(Math.max(size,1),50);return ResponseEntity.ok(followService.connections(UUID.fromString(a.getName()),PageRequest.of(safePage,safeSize)));}
}
