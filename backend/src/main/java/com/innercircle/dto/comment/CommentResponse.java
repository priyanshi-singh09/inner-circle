package com.innercircle.dto.comment;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String author,
        String content,
        Instant createdAt
) {}
