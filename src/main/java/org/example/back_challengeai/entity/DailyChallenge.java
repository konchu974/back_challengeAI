package org.example.back_challengeai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "daily_challenge",
        indexes = {
                @Index(name = "idx_daily_challenge_user_date", columnList = "user_id, challenge_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyChallenge {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "challenge_text", nullable = false, columnDefinition = "TEXT")
    private String challengeText;

    @Column(name = "challenge_date", nullable = false)
    private LocalDate challengeDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)  // ← Pour Hibernate 6.x !
    @Column(name = "status", nullable = false, columnDefinition = "challenge_status")
    @Builder.Default
    private ChallengeStatus status = ChallengeStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // ========================================================================
    // Méthodes métier
    // ========================================================================

    public void markAsCompleted() {
        this.status = ChallengeStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markAsSkipped() {
        this.status = ChallengeStatus.SKIPPED;
        this.completedAt = null;
    }

    public boolean isCompleted() {
        return this.status == ChallengeStatus.COMPLETED;
    }

    public boolean isPending() {
        return this.status == ChallengeStatus.PENDING;
    }

    public boolean isSkipped() {
        return this.status == ChallengeStatus.SKIPPED;
    }

    public boolean isToday() {
        return this.challengeDate.equals(LocalDate.now());
    }

    // ========================================================================
    // equals() et hashCode()
    // ========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyChallenge)) return false;
        DailyChallenge that = (DailyChallenge) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}