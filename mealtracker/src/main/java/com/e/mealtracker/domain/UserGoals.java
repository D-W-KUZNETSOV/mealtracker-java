package com.e.mealtracker.domain;

import com.e.mealtracker.entity.User;
import com.e.mealtracker.util.ActivityLevel;   // ← добавить импорт
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGoals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double currentWeightKg;

    @Column(nullable = false)
    private double proteinPerKg;

    @Column
    private Integer targetCalories;

    // ✅ ВЕРНУЛИ
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false, length = 20)
    private ActivityLevel activityLevel = ActivityLevel.SEDENTARY;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (activityLevel == null) activityLevel = ActivityLevel.SEDENTARY;
    }
}
