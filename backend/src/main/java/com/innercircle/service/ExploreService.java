package com.innercircle.service;

import com.innercircle.dto.explore.ExplorePostResponse;
import com.innercircle.dto.explore.ExploreUserResponse;
import com.innercircle.entity.Post;
import com.innercircle.entity.User;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ExploreService {
    private static final String PUBLISHED = "PUBLISHED";

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ExploreService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ExplorePostResponse> posts(String emotion, Pageable pageable) {
        if (emotion == null || emotion.isBlank()) {
            return postRepository.findByStatusOrderByCreatedAtDesc(PUBLISHED, pageable)
                    .map(this::toPostResponse);
        }
        return postRepository.findByEmotionIgnoreCaseAndStatusOrderByCreatedAtDesc(
                        emotion.trim().toLowerCase(Locale.ROOT), PUBLISHED, pageable)
                .map(this::toPostResponse);
    }

    @Transactional(readOnly = true)
    public Page<ExploreUserResponse> people(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return userRepository.findByStatusOrderByHandleAsc("ACTIVE", pageable)
                    .map(this::toUserResponse);
        }
        return userRepository.findByHandleContainingIgnoreCaseAndStatusOrderByHandleAsc(
                        query.trim(), "ACTIVE", pageable)
                .map(this::toUserResponse);
    }

    private ExplorePostResponse toPostResponse(Post post) {
        String author = post.isAnonymous() ? "Anonymous" : "@" + post.getUser().getHandle();
        return new ExplorePostResponse(
                post.getId(), author, post.getEmotion(), post.getCircle().getName(),
                post.getContent(), post.getCreatedAt());
    }

    private ExploreUserResponse toUserResponse(User user) {
        return new ExploreUserResponse(user.getId(), "@" + user.getHandle(), user.getCircle().getName());
    }
}
