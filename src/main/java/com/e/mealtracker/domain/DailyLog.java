package com.e.mealtracker.domain;

import com.e.mealtracker.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class DailyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal calories = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal protein = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal fat = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal carbs = BigDecimal.ZERO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyLog other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}



