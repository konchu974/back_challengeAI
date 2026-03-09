package org.example.back_challengeai.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;  // ← Pas LocalDate !
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserPreferences preferences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DailyChallenge> challenges = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_achievement",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    @Builder.Default
    private Set<Achievement> achievements = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Notification> notifications = new  ArrayList<>();


    // ========================================================================
    // Méthodes helper pour gérer les relations bidirectionnelles
    // ========================================================================

    public void addChallenge(DailyChallenge dailyChallenge) {
        this.challenges.add(dailyChallenge);
        dailyChallenge.setUser(this);
    }

    public void removeChallenge(DailyChallenge dailyChallenge) {
        this.challenges.remove(dailyChallenge);
        dailyChallenge.setUser(null);
    }

    public void unlockAchievement(Achievement achievement) {
        this.achievements.add(achievement);
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setUser(this);
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
        if (this.preferences != null) {
            this.preferences.setUser(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}