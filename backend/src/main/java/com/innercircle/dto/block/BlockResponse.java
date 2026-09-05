package com.innercircle.dto.block;

import java.util.UUID;

public record BlockResponse(UUID userId, String handle, boolean blocked) {}
