package com.innercircle.service;

import com.innercircle.dto.block.BlockResponse;
import com.innercircle.entity.Block;
import com.innercircle.entity.User;
import com.innercircle.repository.BlockRepository;
import com.innercircle.repository.FollowRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BlockService {
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public BlockService(BlockRepository blockRepository, FollowRepository followRepository,
                        UserRepository userRepository) {
        this.blockRepository = blockRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BlockResponse block(UUID blockerId, UUID targetId) {
        if (blockerId.equals(targetId)) {
            throw new IllegalArgumentException("You cannot block yourself.");
        }
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        followRepository.deleteByFollower_IdAndFollowing_Id(blockerId, targetId);
        followRepository.deleteByFollower_IdAndFollowing_Id(targetId, blockerId);

        if (!blockRepository.existsByBlocker_IdAndBlocked_Id(blockerId, targetId)) {
            Block block = new Block();
            block.setBlocker(blocker);
            block.setBlocked(target);
            blockRepository.save(block);
        }
        return new BlockResponse(target.getId(), target.getHandle(), true);
    }

    @Transactional
    public BlockResponse unblock(UUID blockerId, UUID targetId) {
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        blockRepository.deleteByBlocker_IdAndBlocked_Id(blockerId, targetId);
        return new BlockResponse(target.getId(), target.getHandle(), false);
    }

    @Transactional(readOnly = true)
    public BlockResponse status(UUID blockerId, UUID targetId) {
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return new BlockResponse(target.getId(), target.getHandle(),
                blockRepository.existsByBlocker_IdAndBlocked_Id(blockerId, targetId));
    }
}
