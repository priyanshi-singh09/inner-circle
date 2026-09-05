package com.innercircle.controller;

import com.innercircle.dto.comment.CommentResponse;
import com.innercircle.dto.comment.CreateCommentRequest;
import com.innercircle.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService){this.commentService=commentService;}
    @PostMapping public ResponseEntity<CommentResponse> create(Authentication authentication,@PathVariable UUID postId,@Valid @RequestBody CreateCommentRequest request){UUID userId=UUID.fromString(authentication.getName());return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(userId,postId,request));}
    @GetMapping public ResponseEntity<Page<CommentResponse>> getComments(Authentication authentication,@PathVariable UUID postId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){UUID userId=UUID.fromString(authentication.getName());int safePage=Math.max(page,0),safeSize=Math.min(Math.max(size,1),50);return ResponseEntity.ok(commentService.getForPost(userId,postId,PageRequest.of(safePage,safeSize)));}
    @DeleteMapping("/{commentId}") public ResponseEntity<Void> delete(Authentication authentication,@PathVariable UUID commentId){UUID userId=UUID.fromString(authentication.getName());commentService.delete(userId,commentId);return ResponseEntity.noContent().build();}
}
