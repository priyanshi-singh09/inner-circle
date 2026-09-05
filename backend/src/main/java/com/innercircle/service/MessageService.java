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
    private final MessageRepository messageRepository; private final UserRepository userRepository; private final FollowRepository followRepository; private final BlockRepository blockRepository;
    public MessageService(MessageRepository messageRepository,UserRepository userRepository,FollowRepository followRepository,BlockRepository blockRepository){this.messageRepository=messageRepository;this.userRepository=userRepository;this.followRepository=followRepository;this.blockRepository=blockRepository;}
    @Transactional public MessageResponse send(UUID senderId,UUID recipientId,SendMessageRequest request){if(senderId.equals(recipientId))throw new IllegalArgumentException("You cannot message yourself.");User sender=activeUser(senderId);User recipient=activeUser(recipientId);ensureSameCircle(sender,recipient);ensureMutualConnection(senderId,recipientId);ensureNotBlocked(senderId,recipientId);Message message=new Message();message.setSender(sender);message.setRecipient(recipient);message.setContent(request.getContent().trim());return toResponse(messageRepository.save(message));}
    @Transactional(readOnly=true) public Page<MessageResponse> conversation(UUID userId,UUID otherId,Pageable pageable){User user=activeUser(userId);User other=activeUser(otherId);ensureSameCircle(user,other);ensureMutualConnection(userId,otherId);ensureNotBlocked(userId,otherId);return messageRepository.findConversation(userId,otherId,pageable).map(this::toResponse);}
    @Transactional public void markRead(UUID userId,UUID messageId){activeUser(userId);Message message=messageRepository.findById(messageId).orElseThrow(()->new IllegalArgumentException("Message not found."));if(!message.getRecipient().getId().equals(userId))throw new IllegalArgumentException("You can only mark messages sent to you as read.");message.setRead(true);}
    private User activeUser(UUID id){User user=userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User not found."));if(!"ACTIVE".equals(user.getStatus()))throw new IllegalArgumentException("This account is not available for messaging.");return user;}
    private void ensureSameCircle(User a,User b){if(!a.getCircle().getId().equals(b.getCircle().getId()))throw new IllegalArgumentException("Messaging is only available within your Circle.");}
    private void ensureMutualConnection(UUID a,UUID b){if(!followRepository.existsByFollower_IdAndFollowing_Id(a,b)||!followRepository.existsByFollower_IdAndFollowing_Id(b,a))throw new IllegalArgumentException("Messaging is available only after a mutual connection.");}
    private void ensureNotBlocked(UUID a,UUID b){if(blockRepository.existsBetween(a,b))throw new IllegalArgumentException("Messaging is unavailable for this connection.");}
    private MessageResponse toResponse(Message message){return new MessageResponse(message.getId(),message.getSender().getId(),"@"+message.getSender().getHandle(),message.getRecipient().getId(),message.getContent(),message.isRead(),message.getCreatedAt());}
}
