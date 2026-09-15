package com.e.mealtracker.entity;

import com.e.mealtracker.util.ActivityLevel;
import com.e.mealtracker.util.Gender;   // ← добавить
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // ✅ БЫЛО: private String gender;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", length = 20)
    private ActivityLevel activityLevel = ActivityLevel.MODERATE;

    private LocalDate dateOfBirth;
}