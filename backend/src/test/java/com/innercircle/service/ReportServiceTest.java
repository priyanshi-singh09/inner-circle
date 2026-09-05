package com.innercircle.service;

import com.innercircle.dto.report.CreateReportRequest;
import com.innercircle.dto.report.ReportResponse;
import com.innercircle.entity.Report;
import com.innercircle.entity.User;
import com.innercircle.repository.CommentRepository;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.ReportRepository;
import com.innercircle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock ReportRepository reportRepository;
    @Mock UserRepository userRepository;
    @Mock PostRepository postRepository;
    @Mock CommentRepository commentRepository;

    @InjectMocks ReportService reportService;

    @Test
    void reportRequiresTarget() {
        CreateReportRequest request = new CreateReportRequest();
        request.setReason("spam");
        UUID reporterId = UUID.randomUUID();
        when(userRepository.findById(reporterId)).thenReturn(Optional.of(mock(User.class)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.create(reporterId, request));
        assertEquals("A user, post, or comment must be reported.", ex.getMessage());
        verifyNoInteractions(reportRepository, postRepository, commentRepository);
    }

    @Test
    void cannotReportYourself() {
        UUID reporterId = UUID.randomUUID();
        User reporter = mock(User.class);
        User target = mock(User.class);
        when(userRepository.findById(reporterId)).thenReturn(Optional.of(reporter));
        when(userRepository.findById(reporterId)).thenReturn(Optional.of(target));

        CreateReportRequest request = new CreateReportRequest();
        request.setReportedUserId(reporterId);
        request.setReason("other");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reportService.create(reporterId, request));
        assertEquals("You cannot report yourself.", ex.getMessage());
        verifyNoInteractions(reportRepository);
    }
}
