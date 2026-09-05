package com.innercircle.service;

import com.innercircle.dto.block.BlockResponse;
import com.innercircle.entity.User;
import com.innercircle.repository.BlockRepository;
import com.innercircle.repository.FollowRepository;
import com.innercircle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {
    @Mock BlockRepository blockRepository;
    @Mock FollowRepository followRepository;
    @Mock UserRepository userRepository;

    @InjectMocks BlockService blockService;

    @Test
    void cannotBlockSelf() {
        UUID id = UUID.randomUUID();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> blockService.block(id, id));
        assertEquals("You cannot block yourself.", ex.getMessage());
        verifyNoInteractions(userRepository, followRepository, blockRepository);
    }

    @Test
    void blockingRemovesBothFollowDirections() {
        UUID blockerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        User blocker = mock(User.class);
        User target = mock(User.class);
        when(blocker.getId()).thenReturn(blockerId);
        when(target.getId()).thenReturn(targetId);
        when(target.getHandle()).thenReturn("bob");
        when(userRepository.findById(blockerId)).thenReturn(Optional.of(blocker));
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(blockRepository.existsByBlocker_IdAndBlocked_Id(blockerId, targetId)).thenReturn(false);

        BlockResponse response = blockService.block(blockerId, targetId);

        assertTrue(response.blocked());
        verify(followRepository).deleteByFollower_IdAndFollowing_Id(blockerId, targetId);
        verify(followRepository).deleteByFollower_IdAndFollowing_Id(targetId, blockerId);
        verify(blockRepository).save(any());
    }
}
