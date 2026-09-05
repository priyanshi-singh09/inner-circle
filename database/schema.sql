-- Inner Circle PostgreSQL schema
-- V1 foundation: identity, circles, posts, support, connections and safety.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE circles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    min_age SMALLINT NOT NULL CHECK (min_age >= 13),
    max_age SMALLINT,
    description TEXT,
    CHECK (max_age IS NULL OR max_age >= min_age)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    handle VARCHAR(30) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    circle_id UUID NOT NULL REFERENCES circles(id),
    bio VARCHAR(160),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DEACTIVATED', 'BANNED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE topics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(80) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    circle_id UUID NOT NULL REFERENCES circles(id),
    topic_id UUID REFERENCES topics(id) ON DELETE SET NULL,
    emotion VARCHAR(40) NOT NULL,
    content TEXT NOT NULL CHECK (length(trim(content)) > 0),
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    content_warning BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED'
        CHECK (status IN ('PUBLISHED', 'HIDDEN', 'REMOVED', 'UNDER_REVIEW')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL CHECK (length(trim(content)) > 0),
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED'
        CHECK (status IN ('PUBLISHED', 'HIDDEN', 'REMOVED', 'UNDER_REVIEW')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE reactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reaction_type VARCHAR(30) NOT NULL
        CHECK (reaction_type IN ('I_HEAR_YOU', 'RELATE', 'ROOTING_FOR_YOU')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (post_id, user_id, reaction_type)
);

CREATE TABLE follows (
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (follower_id, following_id),
    CHECK (follower_id <> following_id)
);

CREATE TABLE blocks (
    blocker_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (blocker_id, blocked_id),
    CHECK (blocker_id <> blocked_id)
);

CREATE TABLE reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reported_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    post_id UUID REFERENCES posts(id) ON DELETE SET NULL,
    comment_id UUID REFERENCES comments(id) ON DELETE SET NULL,
    reason VARCHAR(40) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'REVIEWING', 'RESOLVED', 'DISMISSED')),
    reviewed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    reviewed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (post_id IS NOT NULL OR comment_id IS NOT NULL OR reported_user_id IS NOT NULL)
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(40) NOT NULL,
    reference_id UUID,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE moderation_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    moderator_id UUID NOT NULL REFERENCES users(id),
    target_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    post_id UUID REFERENCES posts(id) ON DELETE SET NULL,
    comment_id UUID REFERENCES comments(id) ON DELETE SET NULL,
    action VARCHAR(40) NOT NULL,
    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Feed and lookup indexes.
CREATE INDEX idx_posts_circle_created ON posts(circle_id, created_at DESC);
CREATE INDEX idx_posts_user_created ON posts(user_id, created_at DESC);
CREATE INDEX idx_posts_emotion_created ON posts(emotion, created_at DESC);
CREATE INDEX idx_posts_topic_created ON posts(topic_id, created_at DESC);
CREATE INDEX idx_comments_post_created ON comments(post_id, created_at ASC);
CREATE INDEX idx_reactions_post_type ON reactions(post_id, reaction_type);
CREATE INDEX idx_follows_following ON follows(following_id);
CREATE INDEX idx_follows_follower ON follows(follower_id);
CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at DESC);
CREATE INDEX idx_reports_status_created ON reports(status, created_at ASC);

-- Useful safety/privacy lookups.
CREATE INDEX idx_blocks_blocked ON blocks(blocked_id);
CREATE INDEX idx_users_circle ON users(circle_id);
