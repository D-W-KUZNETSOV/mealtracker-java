package com.e.mealtracker.service;

import com.e.mealtracker.dto.UserProfileDto;
import com.e.mealtracker.dto.UserProfileUpdateDto;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.exception.ResourceNotFoundException;
import com.e.mealtracker.repository.UserRepository;
import com.e.mealtracker.util.ActivityLevel;
import com.e.mealtracker.util.AgeCalculator;
import com.e.mealtracker.util.BmiCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional
    public void updateProfile(String username, UserProfileUpdateDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        if (dto.getDateOfBirth() != null) {
            profile.setDateOfBirth(dto.getDateOfBirth());
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
            profile.setGender(dto.getGender());   // Gender → Gender
        }
        // ✅ DTO уже хранит ActivityLevel — прямая передача
        if (dto.getActivityLevel() != null) {
            profile.setActivityLevel(dto.getActivityLevel());
        }

        // ✅ страховка на случай, если поле null (старые записи / new UserProfile)
        if (profile.getActivityLevel() == null) {
            profile.setActivityLevel(ActivityLevel.MODERATE);
        }

        log.info("Profile updated for user: {}", username);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        UserProfile profile = user.getProfile();
        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found for user: " + username);
        }

        UserProfileDto dto = new UserProfileDto();
        dto.setHeightCm(profile.getHeightCm());
        dto.setCurrentWeightKg(profile.getCurrentWeightKg());
        dto.setTargetWeightKg(profile.getTargetWeightKg());
        dto.setGender(profile.getGender());
        // ✅ enum → enum, без конвертаций
        dto.setActivityLevel(profile.getActivityLevel());

        if (profile.getDateOfBirth() != null) {
            dto.setAgeYears(AgeCalculator.calculateAge(profile.getDateOfBirth()));
        } else {
            dto.setAgeYears(null);
        }

        dto.setBmi(BmiCalculator.calculate(
                profile.getCurrentWeightKg(),
                profile.getHeightCm()
        ));

        return dto;
    }
}