package org.example.back_challengeai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {

    @Id
    @UuidGenerator
    private UUID id;

    @Type(JsonType.class)
    @Column(name = "interests", columnDefinition = "text")
    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @Type(JsonType.class)
    @Column(name = "goals", columnDefinition = "text")
    @Builder.Default
    private List<String> goals = new ArrayList<>();

    @Builder.Default
    @Column(name = "daily_time")
    private Integer dailyTime = 10;

    @UpdateTimestamp
    @Column(name = "updated_at" )
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    @JsonIgnore
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPreferences)) return false;
        UserPreferences userpref = (UserPreferences) o;
        return id != null && id.equals(userpref.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
