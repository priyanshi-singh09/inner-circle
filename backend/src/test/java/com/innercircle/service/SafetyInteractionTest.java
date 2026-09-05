package com.innercircle.service;

import com.innercircle.dto.comment.CreateCommentRequest;
import com.innercircle.dto.message.SendMessageRequest;
import com.innercircle.entity.Circle;
import com.innercircle.entity.Post;
import com.innercircle.entity.User;
import com.innercircle.repository.BlockRepository;
import com.innercircle.repository.CommentRepository;
import com.innercircle.repository.FollowRepository;
import com.innercircle.repository.MessageRepository;
import com.innercircle.repository.PostRepository;
import com.innercircle.repository.ReactionRepository;
import com.innercircle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @Mock FollowRepository followRepository;
    @Mock MessageRepository messageRepository;
    @Mock NotificationService notificationService;

    @Test
    void blockedUserCannotFollow() {
        UUID viewer=UUID.randomUUID(),target=UUID.randomUUID();
        when(userRepository.findById(viewer)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(target)).thenReturn(Optional.of(new User()));
        when(blockRepository.existsBetween(viewer,target)).thenReturn(true);
        FollowService service=new FollowService(null,userRepository,notificationService,blockRepository);
        assertThrows(SecurityException.class,()->service.follow(viewer,target));
    }

    @Test
    void blockedUserCannotComment() {
        UUID viewer=UUID.randomUUID(),author=UUID.randomUUID(),postId=UUID.randomUUID();
        User user=new User(),postUser=new User();
        setIdForTest(postUser,author);
        Circle circle=new Circle();
        user.setCircle(circle); postUser.setCircle(circle);
        Post post=new Post(); post.setUser(postUser); post.setStatus("PUBLISHED");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(viewer)).thenReturn(Optional.of(user));
        when(blockRepository.existsBetween(viewer,author)).thenReturn(true);
        CommentService service=new CommentService(commentRepository,postRepository,userRepository,notificationService,blockRepository);
        CreateCommentRequest request=new CreateCommentRequest(); request.setContent("hello");
        assertThrows(SecurityException.class,()->service.create(viewer,postId,request));
    }

    @Test
    void differentCircleUsersCannotMessage() {
        UUID senderId=UUID.randomUUID(),recipientId=UUID.randomUUID();
        User sender=user("sender",UUID.randomUUID());
        User recipient=user("recipient",UUID.randomUUID());
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        MessageService service=new MessageService(messageRepository,userRepository,followRepository,blockRepository);
        SendMessageRequest request=new SendMessageRequest(); request.setContent("hello");
        assertThrows(IllegalArgumentException.class,()->service.send(senderId,recipientId,request));
    }

    @Test
    void nonMutualUsersCannotMessage() {
        UUID senderId=UUID.randomUUID(),recipientId=UUID.randomUUID(),circleId=UUID.randomUUID();
        User sender=user("sender",circleId),recipient=user("recipient",circleId);
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(followRepository.existsByFollower_IdAndFollowing_Id(senderId,recipientId)).thenReturn(true);
        when(followRepository.existsByFollower_IdAndFollowing_Id(recipientId,senderId)).thenReturn(false);
        MessageService service=new MessageService(messageRepository,userRepository,followRepository,blockRepository);
        SendMessageRequest request=new SendMessageRequest(); request.setContent("hello");
        assertThrows(IllegalArgumentException.class,()->service.send(senderId,recipientId,request));
    }

    @Test
    void blockedUsersCannotMessage() {
        UUID senderId=UUID.randomUUID(),recipientId=UUID.randomUUID(),circleId=UUID.randomUUID();
        User sender=user("sender",circleId),recipient=user("recipient",circleId);
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(followRepository.existsByFollower_IdAndFollowing_Id(senderId,recipientId)).thenReturn(true);
        when(followRepository.existsByFollower_IdAndFollowing_Id(recipientId,senderId)).thenReturn(true);
        when(blockRepository.existsBetween(senderId,recipientId)).thenReturn(true);
        MessageService service=new MessageService(messageRepository,userRepository,followRepository,blockRepository);
        SendMessageRequest request=new SendMessageRequest(); request.setContent("hello");
        assertThrows(IllegalArgumentException.class,()->service.send(senderId,recipientId,request));
    }

    private User user(String handle,UUID circleId){
        Circle circle=new Circle();
        setIdForTest(circle,circleId);
        User user=new User(); user.setHandle(handle); user.setCircle(circle); user.setStatus("ACTIVE");
        return user;
    }

    private static void setIdForTest(Object entity,UUID id){
        try { var field=entity.getClass().getDeclaredField("id"); field.setAccessible(true); field.set(entity,id); }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }
}
