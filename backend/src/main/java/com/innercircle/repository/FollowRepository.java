package com.innercircle.repository;

import com.innercircle.entity.Follow;
import com.innercircle.entity.FollowId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    boolean existsByFollower_IdAndFollowing_Id(UUID followerId, UUID followingId);
    void deleteByFollower_IdAndFollowing_Id(UUID followerId, UUID followingId);

    @Query("SELECT f FROM Follow f JOIN FETCH f.following u WHERE f.follower.id = :userId ORDER BY f.createdAt DESC")
    Page<Follow> findFollowing(@Param("userId") UUID userId, Pageable pageable);
}
