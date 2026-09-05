package com.innercircle.controller;

import com.innercircle.dto.message.MessageResponse;
import com.innercircle.dto.message.SendMessageRequest;
import com.innercircle.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<MessageResponse> send(
            Authentication authentication,
            @PathVariable UUID userId,
            @Valid @RequestBody SendMessageRequest request) {
        UUID senderId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(messageService.send(senderId, userId, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<MessageResponse>> conversation(
            Authentication authentication,
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        UUID viewerId = UUID.fromString(authentication.getName());
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return ResponseEntity.ok(messageService.conversation(
                viewerId, userId, PageRequest.of(safePage, safeSize)));
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<Void> markRead(
            Authentication authentication,
            @PathVariable UUID messageId) {
        UUID userId = UUID.fromString(authentication.getName());
        messageService.markRead(userId, messageId);
        return ResponseEntity.noContent().build();
    }
}
