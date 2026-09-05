package com.innercircle.dto.reaction;

import jakarta.validation.constraints.NotBlank;

public record ReactionRequest(@NotBlank String reactionType) {}
