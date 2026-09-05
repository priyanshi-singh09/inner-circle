package com.innercircle.dto.explore;

import java.util.UUID;

public record ExploreUserResponse(
        UUID id,
        String handle,
        String circle
) {}
