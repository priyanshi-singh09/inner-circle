package com.innercircle.service;

import com.innercircle.dto.message.MessageResponse;
import com.innercircle.dto.message.SendMessageRequest;
import com.innercircle.entity.Message;
import com.innercircle.entity.User;
import com.innercircle.repository.BlockRepository;
import com.innercircle.repository.FollowRepository;
import com.innercircle.repository.MessageRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BlockRepository blockRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          FollowRepository followRepository,
                          BlockRepository blockRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.blockRepository = blockRepository;
    }

    @Transactional
    public MessageResponse send(UUID senderId, UUID recipientId, SendMessageRequest request) {
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("You cannot message yourself.");
        }
        if (!followRepository.existsByFollower_IdAndFollowing_Id(senderId, recipientId)
                || !followRepository.existsByFollower_IdAndFollowing_Id(recipientId, senderId)) {
            throw new IllegalArgumentException("Messaging is available only after a mutual connection.");
        }
        if (blockRepository.existsByBlocker_IdAndBlocked_Id(senderId, recipientId)
                || blockRepository.existsByBlocker_IdAndBlocked_Id(recipientId, senderId)) {
            throw new IllegalArgumentException("Messaging is unavailable for this connection.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(request.getContent().trim());
        return toResponse(messageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> conversation(UUID userId, UUID otherId, Pageable pageable) {
        if (!followRepository.existsByFollower_IdAndFollowing_Id(userId, otherId)
                || !followRepository.existsByFollower_IdAndFollowing_Id(otherId, userId)) {
            throw new IllegalArgumentException("Messaging is available only after a mutual connection.");
        }
        if (blockRepository.existsByBlocker_IdAndBlocked_Id(userId, otherId)
                || blockRepository.existsByBlocker_IdAndBlocked_Id(otherId, userId)) {
            throw new IllegalArgumentException("Messaging is unavailable for this connection.");
        }
        return messageRepository.findConversation(userId, otherId, pageable).map(this::toResponse);
    }

    @Transactional
    public void markRead(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found."));
        if (!message.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only mark messages sent to you as read.");
        }
        message.setRead(true);
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                "@" + message.getSender().getHandle(),
                message.getRecipient().getId(),
                message.getContent(),
                message.isRead(),
                message.getCreatedAt());
    }
}
