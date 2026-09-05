package com.e.mealtracker.config;

import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.UserRepository; // создай этот репозиторий
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Создаём тестового пользователя, если его ещё нет
        if (!userRepository.existsByUsername("dmitriy")) {
            User user = new User();
            user.setUsername("dmitriy");
            // Хешируем пароль "banana19"
            user.setPassword(passwordEncoder.encode("banana19"));
            user.setRole(Role.USER);
            userRepository.save(user);
        }
    }
}

