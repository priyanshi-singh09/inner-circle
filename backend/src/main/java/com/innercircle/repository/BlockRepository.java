package com.innercircle.repository;

import com.innercircle.entity.Block;
import com.innercircle.entity.BlockId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BlockRepository extends JpaRepository<Block, BlockId> {
    boolean existsByBlocker_IdAndBlocked_Id(UUID blockerId, UUID blockedId);
    void deleteByBlocker_IdAndBlocked_Id(UUID blockerId, UUID blockedId);
}
