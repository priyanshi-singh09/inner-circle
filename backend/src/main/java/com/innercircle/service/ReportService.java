package com.innercircle.service;

import com.innercircle.dto.report.CreateReportRequest;
import com.innercircle.dto.report.ReportResponse;
import com.innercircle.entity.Comment;
import com.innercircle.entity.Post;
import com.innercircle.entity.Report;
import com.innercircle.entity.User;
import com.innercircle.repository.CommentRepository;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.ReportRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository,
                         PostRepository postRepository, CommentRepository commentRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public ReportResponse create(UUID reporterId, CreateReportRequest request) {
        if (request.getReportedUserId() == null && request.getPostId() == null && request.getCommentId() == null) {
            throw new IllegalArgumentException("A user, post, or comment must be reported.");
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Report report = new Report();
        report.setReporter(reporter);

        if (request.getReportedUserId() != null) {
            User target = userRepository.findById(request.getReportedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Reported user not found."));
            if (reporterId.equals(target.getId())) {
                throw new IllegalArgumentException("You cannot report yourself.");
            }
            report.setReportedUser(target);
        }

        if (request.getPostId() != null) {
            Post post = postRepository.findById(request.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("Post not found."));
            report.setPost(post);
        }

        if (request.getCommentId() != null) {
            Comment comment = commentRepository.findById(request.getCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found."));
            report.setComment(comment);
        }

        report.setReason(request.getReason().trim());
        report.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        return toResponse(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public Page<ReportResponse> getMyReports(UUID reporterId, Pageable pageable) {
        return reportRepository.findByReporter_IdOrderByCreatedAtDesc(reporterId, pageable)
                .map(this::toResponse);
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReportedUser() == null ? null : report.getReportedUser().getId(),
                report.getPost() == null ? null : report.getPost().getId(),
                report.getComment() == null ? null : report.getComment().getId(),
                report.getReason(), report.getStatus(), report.getCreatedAt());
    }
}
