package com.innercircle.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateReportRequest {
    private UUID reportedUserId;
    private UUID postId;
    private UUID commentId;

    @NotBlank
    @Size(max = 40)
    private String reason;

    @Size(max = 1000)
    private String description;

    public UUID getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(UUID reportedUserId) { this.reportedUserId = reportedUserId; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public UUID getCommentId() { return commentId; }
    public void setCommentId(UUID commentId) { this.commentId = commentId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
