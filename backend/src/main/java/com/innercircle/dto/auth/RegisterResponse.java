package com.innercircle.dto.auth;

import java.util.UUID;

public class RegisterResponse {
    private final String message;
    private final UserSummary user;

    public RegisterResponse(String message, UserSummary user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() { return message; }
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
