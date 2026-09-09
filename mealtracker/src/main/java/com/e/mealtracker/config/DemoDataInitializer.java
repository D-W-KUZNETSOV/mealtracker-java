package com.e.mealtracker.config;

import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.repository.UserRepository; // создай этот репозиторий
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("dmitriy")) {
            User dmitriy = new User();
            dmitriy.setUsername("dmitriy");
            dmitriy.setPassword(passwordEncoder.encode("banana19"));
            dmitriy.setRole(Role.USER);
            dmitriy.setEmail("torgor_8@mail.ru");

            UserProfile profile = new UserProfile();
            profile.setHeightCm(178);
            profile.setTargetWeightKg(new BigDecimal("75.0"));
            profile.setCurrentWeightKg(new BigDecimal("81.0")); // <-- текущий вес
            profile.setGender("MALE");
            profile.setActivityLevel("MODERATE");
            profile.setDateOfBirth(LocalDate.of(1995, 5, 20));

            dmitriy.setProfile(profile);
            userRepository.save(dmitriy);
            return;
        } else {
            // Для существующих пользователей без профиля — создаём пустой профиль
            userRepository.findAll().forEach(u -> {
                if (u.getProfile() == null) {
                    UserProfile p = new UserProfile();
                    p.setGender("MALE");
                    p.setActivityLevel("MODERATE");
                    u.setProfile(p);
                    userRepository.save(u);
                }
            });
        }

    }
}




