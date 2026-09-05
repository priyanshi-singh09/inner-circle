package com.innercircle.dto.reaction;

import java.util.Map;

public record ReactionResponse(Map<String, Long> counts, String myReaction) {}
