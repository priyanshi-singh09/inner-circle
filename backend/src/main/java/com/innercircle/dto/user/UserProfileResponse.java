package com.innercircle.dto.user;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String handle,
        String bio,
        String circle,
        String status
) {}
