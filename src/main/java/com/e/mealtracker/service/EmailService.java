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
    /**
     * Отправка фидбека от пользователя.
     * Идёт на адрес support-почты (тот же fromEmail, если не задан отдельный).
     *
     * @param fromUsername   имя залогиненного пользователя
     * @param fromUserEmail  email пользователя из профиля (может быть null)
     * @param contactEmail   email, указанный в форме (может быть null)
     * @param message        текст фидбека
     */
    public void sendFeedback(String fromUsername,
                             String fromUserEmail,
                             String contactEmail,
                             String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromEmail);
            mail.setTo(fromEmail);   // шлём себе
            mail.setSubject("Фидбек от " + fromUsername + " — Баланс");
            mail.setText(buildFeedbackText(fromUsername, fromUserEmail, contactEmail, message));
            mailSender.send(mail);
            log.info("Feedback sent from user '{}'", fromUsername);
        } catch (Exception e) {
            log.error("Failed to send feedback from '{}': {}", fromUsername, e.getMessage());
        }
    }

    private String buildFeedbackText(String username,
                                     String userEmail,
                                     String contactEmail,
                                     String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("Фидбек от пользователя приложения «Баланс».\n\n");
        sb.append("Username: ").append(username).append("\n");
        sb.append("Email в профиле: ")
                .append(userEmail != null ? userEmail : "—")
                .append("\n");
        sb.append("Email для связи (указан в форме): ")
                .append(contactEmail != null && !contactEmail.isBlank() ? contactEmail : "—")
                .append("\n\n");
        sb.append("Сообщение:\n");
        sb.append("---\n");
        sb.append(message).append("\n");
        sb.append("---\n\n");
        sb.append("Отправлено: ").append(java.time.LocalDateTime.now()).append("\n");
        return sb.toString();
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