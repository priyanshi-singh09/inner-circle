package com.innercircle.controller;

import com.innercircle.dto.explore.ExplorePostResponse;
import com.innercircle.dto.explore.ExploreUserResponse;
import com.innercircle.service.ExploreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/explore")
public class ExploreController {
    private final ExploreService exploreService;
    public ExploreController(ExploreService exploreService){this.exploreService=exploreService;}
    @GetMapping("/posts") public ResponseEntity<Page<ExplorePostResponse>> posts(Authentication authentication,@RequestParam(required=false) String emotion,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){UUID viewerId=UUID.fromString(authentication.getName());int safePage=Math.max(page,0),safeSize=Math.min(Math.max(size,1),50);return ResponseEntity.ok(exploreService.posts(viewerId,emotion,PageRequest.of(safePage,safeSize)));}
    @GetMapping("/people") public ResponseEntity<Page<ExploreUserResponse>> people(Authentication authentication,@RequestParam(required=false) String q,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){UUID viewerId=UUID.fromString(authentication.getName());int safePage=Math.max(page,0),safeSize=Math.min(Math.max(size,1),50);return ResponseEntity.ok(exploreService.people(viewerId,q,PageRequest.of(safePage,safeSize)));}
}
