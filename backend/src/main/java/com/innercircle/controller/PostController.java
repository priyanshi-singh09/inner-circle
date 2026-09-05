package com.innercircle.controller;

import com.innercircle.dto.post.CreatePostRequest;
import com.innercircle.dto.post.PostResponse;
import com.innercircle.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreatePostRequest request) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(userId, request));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> feed(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID userId = UUID.fromString(authentication.getName());
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return ResponseEntity.ok(postService.feed(userId, PageRequest.of(safePage, safeSize)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> get(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.get(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable UUID postId) {
        UUID userId = UUID.fromString(authentication.getName());
        postService.delete(userId, postId);
        return ResponseEntity.noContent().build();
    }
}
