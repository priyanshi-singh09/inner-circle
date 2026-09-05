package com.innercircle.repository;

import com.innercircle.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByPost_IdAndStatusOrderByCreatedAtAsc(UUID postId,String status,Pageable pageable);
    long countByPost_IdAndStatus(UUID postId,String status);

    @Query("""
            SELECT c FROM Comment c
            WHERE c.post.id = :postId
              AND c.status = 'PUBLISHED'
              AND NOT EXISTS (
                SELECT b FROM Block b
                WHERE (b.blocker.id = :viewerId AND b.blocked.id = c.user.id)
                   OR (b.blocker.id = c.user.id AND b.blocked.id = :viewerId)
              )
            ORDER BY c.createdAt ASC
            """)
    Page<Comment> findVisibleComments(@Param("postId") UUID postId,@Param("viewerId") UUID viewerId,Pageable pageable);
}
