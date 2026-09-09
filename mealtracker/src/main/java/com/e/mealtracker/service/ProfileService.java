package com.e.mealtracker.service;

import com.e.mealtracker.dto.UserProfileDto;
import com.e.mealtracker.dto.UserProfileUpdateDto;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional
    public void updateProfile(String username, UserProfileUpdateDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        UserProfile profile = user.getProfile();

        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        if (dto.getHeightCm() != null) {
            profile.setHeightCm(dto.getHeightCm());
        }
        if (dto.getCurrentWeightKg() != null) {
            profile.setCurrentWeightKg(dto.getCurrentWeightKg());
        }
        if (dto.getTargetWeightKg() != null) {
            profile.setTargetWeightKg(dto.getTargetWeightKg());
        }
        if (dto.getGender() != null) {
            profile.setGender(dto.getGender());
        }
        if (dto.getActivityLevel() != null) {
            profile.setActivityLevel(dto.getActivityLevel());
        }

        userRepository.save(user);
    }
    @Transactional(readOnly = true)
    public UserProfileDto getProfile(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        var profile = user.getProfile();
        if (profile == null) {
            return new UserProfileDto(); // или кинуть ошибку, если профиль обязателен
        }

        UserProfileDto dto = new UserProfileDto();
        dto.setHeightCm(profile.getHeightCm());
        dto.setCurrentWeightKg(profile.getCurrentWeightKg());
        dto.setTargetWeightKg(profile.getTargetWeightKg());
        dto.setGender(profile.getGender());
        dto.setActivityLevel(profile.getActivityLevel());

        // Возраст
        if (profile.getDateOfBirth() != null) {
            int age = java.time.Period.between(profile.getDateOfBirth(), java.time.LocalDate.now()).getYears();
            dto.setAgeYears(age);
        }

        // ИМТ: вес (кг) / (рост (м) ^ 2)
        if (profile.getCurrentWeightKg() != null && profile.getHeightCm() != null) {
            BigDecimal heightM = new BigDecimal(profile.getHeightCm()).divide(new BigDecimal(100));
            BigDecimal bmi = profile.getCurrentWeightKg().divide(heightM.pow(2), java.math.MathContext.DECIMAL32);
            dto.setBmi(bmi);
        }

        return dto;
    }

}

