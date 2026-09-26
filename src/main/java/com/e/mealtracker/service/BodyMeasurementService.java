package com.e.mealtracker.service;

import com.e.mealtracker.dto.BodyMeasurementDto;
import com.e.mealtracker.dto.CreateBodyMeasurementRequest;
import com.e.mealtracker.entity.BodyMeasurement;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.ResourceNotFoundException;
import com.e.mealtracker.repository.BodyMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodyMeasurementService {

    private final BodyMeasurementRepository repository;

    @Transactional(readOnly = true)
    public List<BodyMeasurementDto> listByUser(User user) {
        return repository.findByUserOrderByMeasuredAtDesc(user)
                .stream()
                .map(BodyMeasurementDto::fromEntity)
                .toList();
    }

    @Transactional
    public BodyMeasurementDto create(User user, CreateBodyMeasurementRequest req) {
        BodyMeasurement m = new BodyMeasurement();
        m.setUser(user);
        m.setMeasuredAt(req.getMeasuredAt());
        m.setWeightKg(req.getWeightKg());
        m.setChestCm(req.getChestCm());
        m.setWaistCm(req.getWaistCm());
        m.setBellyCm(req.getBellyCm());
        m.setHipsCm(req.getHipsCm());
        m.setThighCm(req.getThighCm());
        m.setArmCm(req.getArmCm());
        m.setNeckCm(req.getNeckCm());
        m.setNote(req.getNote());
        return BodyMeasurementDto.fromEntity(repository.save(m));
    }

    @Transactional
    public void delete(User user, Long id) {
        BodyMeasurement m = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Замер не найден: " + id));
        repository.delete(m);
    }
}