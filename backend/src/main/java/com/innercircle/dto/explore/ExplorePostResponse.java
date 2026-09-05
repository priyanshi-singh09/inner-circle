package com.innercircle.dto.explore;

import java.time.Instant;
import java.util.UUID;

public record ExplorePostResponse(
        UUID id,
        String author,
        String emotion,
        String circle,
        String content,
        Instant createdAt
) {}
