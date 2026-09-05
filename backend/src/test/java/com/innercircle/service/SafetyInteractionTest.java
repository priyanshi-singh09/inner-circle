package com.innercircle.service;

import com.innercircle.entity.Post;
import com.innercircle.entity.User;
import com.innercircle.repository.BlockRepository;
import com.innercircle.repository.CommentRepository;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.ReactionRepository;
import com.innercircle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SafetyInteractionTest {
    @Mock BlockRepository blockRepository;
    @Mock PostRepository postRepository;
    @Mock UserRepository userRepository;
    @Mock ReactionRepository reactionRepository;
    @Mock CommentRepository commentRepository;
    @Mock NotificationService notificationService;

    @Test
    void blockedUserCannotFollow() {
        UUID viewer = UUID.randomUUID(), target = UUID.randomUUID();
        User a = new User(), b = new User();
        when(userRepository.findById(viewer)).thenReturn(Optional.of(a));
        when(userRepository.findById(target)).thenReturn(Optional.of(b));
        when(blockRepository.existsBetween(viewer, target)).thenReturn(true);
        FollowService service = new FollowService(null, userRepository, notificationService, blockRepository);
        assertThrows(SecurityException.class, () -> service.follow(viewer, target));
    }

    @Test
    void blockedUserCannotComment() {
        UUID viewer = UUID.randomUUID(), author = UUID.randomUUID(), postId = UUID.randomUUID();
        User user = new User(), postUser = new User();
        Post post = new Post();
        post.setUser(postUser);
        post.setStatus("PUBLISHED");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(viewer)).thenReturn(Optional.of(user));
        when(blockRepository.existsBetween(viewer, author)).thenReturn(true);
        postUser.setId(author);
        CommentService service = new CommentService(commentRepository, postRepository, userRepository, notificationService, blockRepository);
        assertThrows(SecurityException.class, () -> service.create(viewer, postId, new com.innercircle.dto.comment.CreateCommentRequest("hello", false)));
    }
}
