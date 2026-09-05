package com.innercircle.dto.auth;

import java.util.UUID;

public class LoginResponse {
    private final String token;
    private final String tokenType;
    private final UserSummary user;

    public LoginResponse(String token, String tokenType, UserSummary user) {
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public UserSummary getUser() { return user; }

    public static class UserSummary {
        private final UUID id;
        private final String handle;
        private final String circle;

        public UserSummary(UUID id, String handle, String circle) {
            this.id = id;
            this.handle = handle;
            this.circle = circle;
        }

        public UUID getId() { return id; }
        public String getHandle() { return handle; }
        public String getCircle() { return circle; }
    }
}
