package com.innercircle.controller;

import com.innercircle.dto.block.BlockResponse;
import com.innercircle.service.BlockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class BlockController {
    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<BlockResponse> block(Authentication authentication, @PathVariable UUID userId) {
        return ResponseEntity.ok(blockService.block(UUID.fromString(authentication.getName()), userId));
    }

    @DeleteMapping("/{userId}/block")
    public ResponseEntity<BlockResponse> unblock(Authentication authentication, @PathVariable UUID userId) {
        return ResponseEntity.ok(blockService.unblock(UUID.fromString(authentication.getName()), userId));
    }

    @GetMapping("/{userId}/block")
    public ResponseEntity<BlockResponse> status(Authentication authentication, @PathVariable UUID userId) {
        return ResponseEntity.ok(blockService.status(UUID.fromString(authentication.getName()), userId));
    }
}
