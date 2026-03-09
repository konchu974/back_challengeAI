package org.example.back_challengeai.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "achievement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;


    @Column(name = "name" ,unique = true, nullable = false, length = 100)
    private String name;


    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String icon;


    @Column(name = "unlock_criteria", columnDefinition = "TEXT")
    private String unlockCriteria;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement)) return false;
        Achievement that = (Achievement) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}