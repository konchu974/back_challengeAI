package org.example.back_challengeai.repository;

import org.example.back_challengeai.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUser_Id(UUID userId);

    List<Notification> findByUser_IdAndReadAtIsNull(UUID userId);

    long countByUser_IdAndReadAtIsNull(UUID userId);
}
