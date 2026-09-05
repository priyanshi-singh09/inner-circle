package com.innercircle.controller;

import com.innercircle.dto.reaction.ReactionRequest;
import com.innercircle.dto.reaction.ReactionResponse;
import com.innercircle.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts/{postId}/reactions")
public class ReactionController {
    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping
    public ResponseEntity<ReactionResponse> add(
            Authentication authentication,
            @PathVariable UUID postId,
            @Valid @RequestBody ReactionRequest request) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(reactionService.add(userId, postId, request.reactionType()));
    }

    @DeleteMapping("/{reactionType}")
    public ResponseEntity<ReactionResponse> remove(
            Authentication authentication,
            @PathVariable UUID postId,
            @PathVariable String reactionType) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(reactionService.remove(userId, postId, reactionType));
    }

    @GetMapping
    public ResponseEntity<ReactionResponse> get(
            Authentication authentication,
            @PathVariable UUID postId) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(reactionService.get(userId, postId));
    }
}
