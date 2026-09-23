package com.e.mealtracker.entity;

import com.e.mealtracker.util.ActivityLevel;
import com.e.mealtracker.util.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")   // ← исключаем обратную ссылку
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer heightCm;
    private BigDecimal targetWeightKg;
    private BigDecimal currentWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", length = 20)
    private ActivityLevel activityLevel = ActivityLevel.MODERATE;

    private LocalDate dateOfBirth;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}