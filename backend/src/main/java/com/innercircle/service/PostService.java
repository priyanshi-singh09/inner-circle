package com.innercircle.service;

import com.innercircle.dto.post.CreatePostRequest;
import com.innercircle.dto.post.PostResponse;
import com.innercircle.entity.Post;
import com.innercircle.entity.User;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PostResponse create(UUID userId, CreatePostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Post post = new Post();
        post.setUser(user);
        post.setCircle(user.getCircle());
        post.setContent(request.getContent().trim());
        post.setEmotion(request.getEmotion().trim());
        post.setAnonymous(request.isAnonymous());
        post.setContentWarning(false);
        post.setStatus("PUBLISHED");

        return toResponse(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> feed(UUID viewerId, Pageable pageable) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return postRepository.findPersonalizedFeed(
                        viewerId,
                        viewer.getCircle().getId(),
                        pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PostResponse get(UUID postId) {
        return postRepository.findById(postId)
                .filter(post -> "PUBLISHED".equals(post.getStatus()))
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));
    }

    @Transactional
    public void delete(UUID userId, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("You can only delete your own post.");
        }
        post.setStatus("REMOVED");
        postRepository.save(post);
    }

    private PostResponse toResponse(Post post) {
        String author = post.isAnonymous() ? "Anonymous" : "@" + post.getUser().getHandle();
        return new PostResponse(
                post.getId(),
                author,
                post.getEmotion(),
                post.getCircle().getName(),
                post.getContent(),
                post.getCreatedAt()
        );
    }
}
