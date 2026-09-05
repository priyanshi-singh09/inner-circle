package com.innercircle.dto.message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID senderId,
        String sender,
        UUID recipientId,
        String content,
        boolean read,
        Instant createdAt
) {}
