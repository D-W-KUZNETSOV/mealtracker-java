package com.e.mealtracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Отправка простого текстового письма.
     * Если SMTP падает — логируем ошибку, но НЕ пробрасываем (чтобы
     * пользователь всё равно получил 204).
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Восстановление пароля — Баланс");
            message.setText(buildResetText(resetToken));
            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            // Не пробрасываем — endpoint вернёт 204, письмо могло не дойти
        }
    }

    private String buildResetText(String token) {
        String link = "https://mealtrackerfr-d-w-kuznetsov.amvera.io/reset-password?token=" + token;
        return "Здравствуйте!\n\n"
                + "Вы запросили сброс пароля в приложении «Баланс».\n\n"
                + "Перейдите по ссылке, чтобы задать новый пароль:\n"
                + link + "\n\n"
                + "Ссылка действует 1 час.\n"
                + "Если вы не запрашивали сброс — просто проигнорируйте это письмо.\n\n"
                + "— Баланс";
    }
}