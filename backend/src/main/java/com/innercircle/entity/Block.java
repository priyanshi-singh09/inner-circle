package com.innercircle.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "blocks")
@IdClass(BlockId.class)
public class Block {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blocked_id")
    private User blocked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public User getBlocker() { return blocker; }
    public void setBlocker(User blocker) { this.blocker = blocker; }
    public User getBlocked() { return blocked; }
    public void setBlocked(User blocked) { this.blocked = blocked; }
}
