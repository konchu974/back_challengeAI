package org.example.back_challengeai.service;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.entity.Notification;
import org.example.back_challengeai.entity.NotificationType;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.repository.NotificationRepository;
import org.example.back_challengeai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Notification createNotification(UUID userId, String message, NotificationType type) {

        User user = userRepository.findById(userId).orElseThrow();

        Notification notification= Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .build();

        return notificationRepository.save(notification);
    }


    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUser_Id(userId);
    }


    public List<Notification> getUnreadNotifications(UUID userId) {
       return notificationRepository.findByUser_IdAndReadAtIsNull(userId);
    }


    public Notification markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("notification not found"));

        if (notification.isRead()) {
            throw new RuntimeException("notification is read");
        }

        notification.markAsRead();

        return notificationRepository.save(notification);
    }


    public long countUnreadNotifications(UUID userId) {
        return notificationRepository.countByUser_IdAndReadAtIsNull(userId);
    }
}