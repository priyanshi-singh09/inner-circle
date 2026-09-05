package com.innercircle.controller;

import com.innercircle.dto.explore.ExplorePostResponse;
import com.innercircle.dto.explore.ExploreUserResponse;
import com.innercircle.service.ExploreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/explore")
public class ExploreController {
    private final ExploreService exploreService;

    public ExploreController(ExploreService exploreService) {
        this.exploreService = exploreService;
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<ExplorePostResponse>> posts(
            @RequestParam(required = false) String emotion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return ResponseEntity.ok(exploreService.posts(
                emotion, PageRequest.of(safePage, safeSize)));
    }

    @GetMapping("/people")
    public ResponseEntity<Page<ExploreUserResponse>> people(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return ResponseEntity.ok(exploreService.people(
                q, PageRequest.of(safePage, safeSize)));
    }
}
