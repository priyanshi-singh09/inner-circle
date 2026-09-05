package com.innercircle.dto.user;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    @Size(min = 3, max = 30)
    private String handle;

    @Size(max = 160)
    private String bio;

    public String getHandle() { return handle; }
    public void setHandle(String handle) { this.handle = handle; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
