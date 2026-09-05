package com.innercircle.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {
    @NotBlank
    @Size(max = 2000)
    private String content;

    private boolean anonymous;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
}
