package com.innercircle.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "follows")
public class Follow {
    @EmbeddedId
    private FollowId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("followerId")
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("followingId")
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Follow() {}

    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
        this.id = new FollowId(follower.getId(), following.getId());
    }

    public FollowId getId() { return id; }
    public User getFollower() { return follower; }
    public User getFollowing() { return following; }
    public Instant getCreatedAt() { return createdAt; }
}

@Embeddable
class FollowId implements java.io.Serializable {
    @Column(name = "follower_id")
    private UUID followerId;

    @Column(name = "following_id")
    private UUID followingId;

    public FollowId() {}

    public FollowId(UUID followerId, UUID followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public UUID getFollowerId() { return followerId; }
    public UUID getFollowingId() { return followingId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FollowId other)) return false;
        return java.util.Objects.equals(followerId, other.followerId)
                && java.util.Objects.equals(followingId, other.followingId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(followerId, followingId);
    }
}
