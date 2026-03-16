package org.example.back_challengeai.unit;

import org.example.back_challengeai.entity.Notification;
import org.example.back_challengeai.entity.NotificationType;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.repository.NotificationRepository;
import org.example.back_challengeai.repository.UserRepository;
import org.example.back_challengeai.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .pseudo("TestUser")
                .password("hashedPassword")
                .build();

        testNotification = Notification.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .message("Test notification")
                .type(NotificationType.REMINDER)
                .build();
    }

    @Test
    void createNotification_ShouldCreateAndSave() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        Notification result = notificationService.createNotification(
                testUser.getId(), "Test notification", NotificationType.REMINDER);

        // Then
        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getUserNotifications_ShouldReturnAllNotifications() {
        // Given
        when(notificationRepository.findByUser_Id(testUser.getId()))
                .thenReturn(List.of(testNotification));

        // When
        List<Notification> result = notificationService.getUserNotifications(testUser.getId());

        // Then
        assertEquals(1, result.size());
        verify(notificationRepository).findByUser_Id(testUser.getId());
    }

    @Test
    void getUnreadNotifications_ShouldReturnOnlyUnread() {
        // Given
        when(notificationRepository.findByUser_IdAndReadAtIsNull(testUser.getId()))
                .thenReturn(List.of(testNotification));

        // When
        List<Notification> result = notificationService.getUnreadNotifications(testUser.getId());

        // Then
        assertEquals(1, result.size());
        assertNull(result.get(0).getReadAt());
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        // Given
        when(notificationRepository.findById(testNotification.getId()))
                .thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        Notification result = notificationService.markAsRead(testNotification.getId());

        // Then
        assertNotNull(result);
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void markAsRead_ShouldThrow_WhenAlreadyRead() {
        // Given
        testNotification.setReadAt(LocalDateTime.now());
        when(notificationRepository.findById(testNotification.getId()))
                .thenReturn(Optional.of(testNotification));

        // When & Then
        assertThrows(RuntimeException.class, () ->
                notificationService.markAsRead(testNotification.getId()));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void countUnreadNotifications_ShouldReturnCount() {
        // Given
        when(notificationRepository.countByUser_IdAndReadAtIsNull(testUser.getId())).thenReturn(3L);

        // When
        long count = notificationService.countUnreadNotifications(testUser.getId());

        // Then
        assertEquals(3L, count);
    }
}
