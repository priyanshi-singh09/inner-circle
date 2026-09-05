package com.innercircle.repository;

import com.innercircle.entity.Block;
import com.innercircle.entity.BlockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BlockRepository extends JpaRepository<Block, BlockId> {
    boolean existsByBlocker_IdAndBlocked_Id(UUID blockerId, UUID blockedId);
    void deleteByBlocker_IdAndBlocked_Id(UUID blockerId, UUID blockedId);

    @Query("SELECT COUNT(b) > 0 FROM Block b WHERE (b.blocker.id = :userA AND b.blocked.id = :userB) OR (b.blocker.id = :userB AND b.blocked.id = :userA)")
    boolean existsBetween(@Param("userA") UUID userA, @Param("userB") UUID userB);
}
