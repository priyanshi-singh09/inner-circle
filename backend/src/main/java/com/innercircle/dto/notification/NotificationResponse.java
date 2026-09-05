package com.innercircle.dto.notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        UUID referenceId,
        String message,
        boolean read,
        Instant createdAt
) {}
