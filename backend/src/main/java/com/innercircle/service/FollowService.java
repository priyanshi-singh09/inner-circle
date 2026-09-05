package com.innercircle.service;

import com.innercircle.dto.follow.FollowResponse;
import com.innercircle.entity.Follow;
import com.innercircle.entity.User;
import com.innercircle.repository.FollowRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FollowResponse follow(UUID followerId, UUID targetId) {
        if (followerId.equals(targetId)) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!followRepository.existsByFollower_IdAndFollowing_Id(followerId, targetId)) {
            followRepository.save(new Follow(follower, target));
        }
        return status(followerId, target);
    }

    @Transactional
    public FollowResponse unfollow(UUID followerId, UUID targetId) {
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        followRepository.deleteByFollower_IdAndFollowing_Id(followerId, targetId);
        return status(followerId, target);
    }

    @Transactional(readOnly = true)
    public FollowResponse status(UUID viewerId, UUID targetId) {
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        boolean following = followRepository.existsByFollower_IdAndFollowing_Id(viewerId, targetId);
        boolean mutual = following && followRepository.existsByFollower_IdAndFollowing_Id(targetId, viewerId);
        return new FollowResponse(target.getId(), target.getHandle(), following, mutual);
    }
}
