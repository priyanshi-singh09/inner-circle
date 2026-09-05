package com.innercircle.repository;

import com.innercircle.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    Optional<Reaction> findByPost_IdAndUser_IdAndReactionType(UUID postId, UUID userId, String reactionType);

    @Query("SELECT r.reactionType, COUNT(r) FROM Reaction r WHERE r.post.id = :postId GROUP BY r.reactionType")
    List<Object[]> countByPostId(@Param("postId") UUID postId);
}
