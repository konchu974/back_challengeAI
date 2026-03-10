package org.example.back_challengeai.controller;

import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.dto.UnreadCountResponse;
import org.example.back_challengeai.entity.Notification;
import org.example.back_challengeai.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping()
    public ResponseEntity<List<Notification>> getUserNotifications(@RequestParam UUID userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestParam UUID userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable UUID id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(@RequestParam UUID userId) {
        long count = notificationService.countUnreadNotifications(userId);
        UnreadCountResponse response = new UnreadCountResponse(count);

        return ResponseEntity.ok(response);
    }
}
