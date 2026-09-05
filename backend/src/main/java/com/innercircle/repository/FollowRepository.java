package com.innercircle.repository;

import com.innercircle.entity.Follow;
import com.innercircle.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    boolean existsByFollower_IdAndFollowing_Id(UUID followerId, UUID followingId);
    void deleteByFollower_IdAndFollowing_Id(UUID followerId, UUID followingId);
}
