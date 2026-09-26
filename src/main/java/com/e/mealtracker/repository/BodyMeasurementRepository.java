package com.e.mealtracker.repository;

import com.e.mealtracker.entity.BodyMeasurement;
import com.e.mealtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {

    List<BodyMeasurement> findByUserOrderByMeasuredAtDesc(User user);

    Optional<BodyMeasurement> findByIdAndUser(Long id, User user);
}
