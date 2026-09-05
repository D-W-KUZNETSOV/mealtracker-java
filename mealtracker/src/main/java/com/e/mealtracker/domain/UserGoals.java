package com.e.mealtracker.domain;

import com.e.mealtracker.util.ActivityLevel;
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

    @Column(nullable = false)
    private double currentWeightKg;

    @Column(nullable = false)
    private double proteinPerKg;

    @Column
    private Integer targetCalories;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    @Enumerated(EnumType.STRING) // лучше хранить как строку: SEDENTARY, LIGHT и т.д.
    private ActivityLevel activityLevel = ActivityLevel.SEDENTARY; // дефолт

}

