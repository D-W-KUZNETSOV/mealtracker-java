package com.e.mealtracker.service;

import com.e.mealtracker.dto.UserProfileDto;
import com.e.mealtracker.dto.UserProfileUpdateDto;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.exception.ResourceNotFoundException;
import com.e.mealtracker.repository.UserRepository;
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
            profile.setGender(dto.getGender());
        }


        // НЕ ставим дефолт здесь — пусть будет в сущности или миграции
        // Если очень надо — вынесите в отдельный метод или оставьте как есть
        if (profile.getActivityLevel() == null) {
            profile.setActivityLevel("MODERATE");
        }

        log.info("Profile updated for user: {}", username);
        // userRepository.save(user) НЕ нужен — вы в @Transactional
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
        dto.setActivityLevel(profile.getActivityLevel());

        // Возраст
        if (profile.getDateOfBirth() != null) {
            dto.setAgeYears(AgeCalculator.calculateAge(profile.getDateOfBirth()));
        } else {
            dto.setAgeYears(null);
        }

        // ✅ ИМТ через BmiCalculator — вся валидация и округление внутри
        dto.setBmi(BmiCalculator.calculate(
                profile.getCurrentWeightKg(),
                profile.getHeightCm()
        ));

        return dto;
    }
}
