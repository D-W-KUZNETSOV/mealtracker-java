package com.e.mealtracker.service;

import com.e.mealtracker.dto.RegisterRequest;
import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.exception.UserAlreadyExistsException;
import com.e.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.e.mealtracker.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // ВАЖНО: здесь в скобках указан параметр RegisterRequest request
    @Transactional
    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже зарегистрирован");
        }


        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);

        // Создаём профиль (дата рождения опциональна — можно заполнить в профиле)
        UserProfile profile = new UserProfile();
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * Soft delete аккаунта. Проверяет пароль, ставит deletedAt.
     * Данные не удаляются — через 30 дней purge (hard delete).
     */
    @Transactional
    public void deleteAccount(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException(
                    "Неверный пароль");
        }

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User '{}' soft-deleted", username);
    }

    /**
     * Hard delete всех пользователей, удалённых более daysOld дней назад.
     * Используется endpoint'ом POST /api/admin/purge-deleted.
     */
    @Transactional
    public int purgeDeletedUsers(int daysOld) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysOld);
        List<User> toDelete = userRepository.findAllByDeletedAtBefore(threshold);
        userRepository.deleteAll(toDelete);
        log.info("Purged {} users deleted before {}", toDelete.size(), threshold);
        return toDelete.size();
    }
    /**
     * Запрос на восстановление пароля.
     * Генерирует токен, сохраняет в БД с TTL 1 час.
     * (Email-отправка добавится позже через EmailService.)
     */
    @Transactional
    public void requestPasswordReset(String email) {
        // Не раскрываем, существует ли email
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("Password reset requested for unknown email: {}", email);
            return;   // тихо игнорируем — защита от перебора
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiresAt(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, token);
    }

    /**
     * Сброс пароля по токену.
     * Проверяет токен + срок, меняет пароль, сбрасывает токен.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Неверный или истёкший токен"));

        if (user.getResetTokenExpiresAt() == null
                || user.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Токен истёк");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        userRepository.save(user);

        log.info("Password reset for user: {}", user.getUsername());
    }
}


