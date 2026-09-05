package com.innercircle.repository;

import com.innercircle.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByHandle(String handle);
    Optional<User> findByEmail(String email);
    Page<User> findByStatusOrderByHandleAsc(String status, Pageable pageable);
    Page<User> findByHandleContainingIgnoreCaseAndStatusOrderByHandleAsc(String handle, String status, Pageable pageable);
    @Query("""
        SELECT u FROM User u
        WHERE u.status = 'ACTIVE' AND u.circle.id = :circleId
          AND (:query IS NULL OR LOWER(u.handle) LIKE LOWER(CONCAT('%', :query, '%')))
          AND u.id <> :viewerId
          AND NOT EXISTS (SELECT b FROM Block b WHERE (b.blocker.id = :viewerId AND b.blocked.id = u.id) OR (b.blocker.id = u.id AND b.blocked.id = :viewerId))
        ORDER BY u.handle ASC
        """)
    Page<User> findExplorePeople(@Param("viewerId") UUID viewerId,@Param("circleId") UUID circleId,@Param("query") String query,Pageable pageable);
}
