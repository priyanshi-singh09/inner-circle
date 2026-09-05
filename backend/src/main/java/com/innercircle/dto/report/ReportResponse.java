package com.innercircle.dto.report;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID reportedUserId,
        UUID postId,
        UUID commentId,
        String reason,
        String status,
        Instant createdAt
) {}
