package com.innercircle.dto.post;

import java.time.Instant;
import java.util.UUID;

public record PostResponse(
        UUID id,
        String author,
        String emotion,
        String circle,
        String content,
        Instant createdAt
) {}
