package com.innercircle.dto.follow;

import java.util.UUID;

public record ConnectionResponse(UUID userId, String handle, String circle, boolean mutualConnection) {}
