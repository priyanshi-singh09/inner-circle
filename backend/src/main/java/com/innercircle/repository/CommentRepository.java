package com.innercircle.repository;

import com.innercircle.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByPost_IdAndStatusOrderByCreatedAtAsc(UUID postId, String status, Pageable pageable);
    long countByPost_IdAndStatus(UUID postId, String status);
}
