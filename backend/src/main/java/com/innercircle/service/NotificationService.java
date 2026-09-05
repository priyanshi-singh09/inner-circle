package com.innercircle.service;

import com.innercircle.dto.notification.NotificationResponse;
import com.innercircle.entity.Notification;
import com.innercircle.entity.User;
import com.innercircle.repository.NotificationRepository;
import com.innercircle.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void create(UUID recipientId, String type, UUID referenceId, String message) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getForUser(UUID userId, Pageable pageable) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public NotificationResponse markRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found."));
        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own notifications.");
        }
        notification.setRead(true);
        return toResponse(notification);
    }

    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(notification -> notification.setRead(true));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(), notification.getType(), notification.getReferenceId(),
                notification.getMessage(), notification.isRead(), notification.getCreatedAt());
    }
}
