package com.e.mealtracker.domain;

import com.e.mealtracker.entity.User;
import com.e.mealtracker.util.GoalType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
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



    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (goalType == null) goalType = GoalType.MAINTAIN;   // ← добавили
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false, length = 20)
    private GoalType goalType = GoalType.MAINTAIN;

    @Column(name = "target_protein_override")
    private Double targetProteinOverride;

    @Column(name = "target_calories_override")
    private Integer targetCaloriesOverride;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGoals other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
