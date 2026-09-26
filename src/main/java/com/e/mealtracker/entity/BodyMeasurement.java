package com.e.mealtracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "body_measurements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class BodyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "measured_at", nullable = false)
    private LocalDate measuredAt;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "chest_cm")
    private BigDecimal chestCm;

    @Column(name = "waist_cm")
    private BigDecimal waistCm;

    @Column(name = "belly_cm")
    private BigDecimal bellyCm;

    @Column(name = "hips_cm")
    private BigDecimal hipsCm;

    @Column(name = "thigh_cm")
    private BigDecimal thighCm;

    @Column(name = "arm_cm")
    private BigDecimal armCm;

    @Column(name = "neck_cm")
    private BigDecimal neckCm;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BodyMeasurement other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}