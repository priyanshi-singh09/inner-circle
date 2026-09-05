package com.innercircle.controller;

import com.innercircle.dto.report.CreateReportRequest;
import com.innercircle.dto.report.ReportResponse;
import com.innercircle.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateReportRequest request) {
        UUID reporterId = UUID.fromString(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.create(reporterId, request));
    }

    @GetMapping("/my-reports")
    public ResponseEntity<Page<ReportResponse>> getMyReports(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID reporterId = UUID.fromString(authentication.getName());
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return ResponseEntity.ok(reportService.getMyReports(
                reporterId, PageRequest.of(safePage, safeSize)));
    }
}
