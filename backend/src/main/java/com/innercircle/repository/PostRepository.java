package com.innercircle.repository;

import com.innercircle.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    Page<Post> findByUser_IdAndStatusOrderByCreatedAtDesc(UUID userId, String status, Pageable pageable);
    Page<Post> findByEmotionIgnoreCaseAndStatusOrderByCreatedAtDesc(String emotion, String status, Pageable pageable);

    @Query("""
            SELECT p FROM Post p
            WHERE p.status = 'PUBLISHED'
              AND p.circle.id = :circleId
              AND NOT EXISTS (
                  SELECT b FROM Block b
                  WHERE (b.blocker.id = :viewerId AND b.blocked.id = p.user.id)
                     OR (b.blocker.id = p.user.id AND b.blocked.id = :viewerId)
              )
            ORDER BY
              CASE
                WHEN p.user.id = :viewerId THEN 0
                WHEN EXISTS (
                    SELECT f.following.id FROM Follow f
                    WHERE f.follower.id = :viewerId AND f.following.id = p.user.id
                ) THEN 1
                ELSE 2
              END,
              p.createdAt DESC
            """)
    Page<Post> findPersonalizedFeed(@Param("viewerId") UUID viewerId,
                                    @Param("circleId") UUID circleId,
                                    Pageable pageable);
}
