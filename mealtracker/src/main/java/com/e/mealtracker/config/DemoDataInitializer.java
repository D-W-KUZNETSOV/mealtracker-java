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
            profile.setGender("MALE");
            profile.setActivityLevel("MODERATE");
            profile.setDateOfBirth(LocalDate.of(1995, 5, 20));

            dmitriy.setProfile(profile); // если в User есть связь
            userRepository.save(dmitriy);
        } else {
            userRepository.findAll().forEach(u -> {
                if (u.getProfile() == null) {
                    UserProfile p = new UserProfile();
                    // только базовые значения, без хардкода личных данных
                    u.setProfile(p);
                    userRepository.save(u);
                }
            });
        }
    }
}




