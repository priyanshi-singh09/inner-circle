package com.innercircle.dto.follow;

import java.util.UUID;

public record FollowResponse(
        UUID userId,
        String handle,
        boolean following,
        boolean mutualConnection
) {}
