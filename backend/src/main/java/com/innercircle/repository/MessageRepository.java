package com.innercircle.repository;

import com.innercircle.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("""
            SELECT m FROM Message m
            WHERE (m.sender.id = :userId AND m.recipient.id = :otherId)
               OR (m.sender.id = :otherId AND m.recipient.id = :userId)
            ORDER BY m.createdAt ASC
            """)
    Page<Message> findConversation(@Param("userId") UUID userId,
                                   @Param("otherId") UUID otherId,
                                   Pageable pageable);
}
