package com.innercircle.entity;

import java.io.Serializable;
import java.util.UUID;

public class BlockId implements Serializable {
    private UUID blocker;
    private UUID blocked;

    public BlockId() {}

    public BlockId(UUID blocker, UUID blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockId other)) return false;
        return java.util.Objects.equals(blocker, other.blocker)
                && java.util.Objects.equals(blocked, other.blocked);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(blocker, blocked);
    }
}
