package com.innercircle.repository;

import com.innercircle.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    Page<Post> findByUser_IdAndStatusOrderByCreatedAtDesc(UUID userId, String status, Pageable pageable);
}
