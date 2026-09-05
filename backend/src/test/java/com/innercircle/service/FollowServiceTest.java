package com.innercircle.service;

import com.innercircle.dto.follow.FollowResponse;
import com.innercircle.entity.User;
import com.innercircle.repository.BlockRepository;
import com.innercircle.repository.FollowRepository;
import com.innercircle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {
    @Mock FollowRepository followRepository;
    @Mock UserRepository userRepository;
    @Mock NotificationService notificationService;
    @Mock BlockRepository blockRepository;

    @InjectMocks FollowService followService;

    @Test
    void cannotFollowSelf() {
        UUID id = UUID.randomUUID();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> followService.follow(id, id));
        assertEquals("You cannot follow yourself.", ex.getMessage());
        verifyNoInteractions(userRepository, followRepository, notificationService, blockRepository);
    }

    @Test
    void followCreatesRelationshipAndNotification() {
        UUID followerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        User follower = mock(User.class);
        User target = mock(User.class);
        when(follower.getId()).thenReturn(followerId);
        when(target.getId()).thenReturn(targetId);
        when(follower.getHandle()).thenReturn("alice");
        when(target.getHandle()).thenReturn("bob");
        when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(blockRepository.existsBetween(followerId, targetId)).thenReturn(false);
        when(followRepository.existsByFollower_IdAndFollowing_Id(followerId, targetId)).thenReturn(false);
        when(followRepository.existsByFollower_IdAndFollowing_Id(targetId, followerId)).thenReturn(true);

        FollowResponse response = followService.follow(followerId, targetId);

        assertTrue(response.following());
        assertTrue(response.mutualConnection());
        verify(followRepository).save(any());
        verify(notificationService).create(targetId, "FOLLOW", followerId, "@alice followed you.");
    }
}
