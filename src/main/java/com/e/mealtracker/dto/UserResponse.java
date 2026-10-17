package com.e.mealtracker.dto;

import com.e.mealtracker.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private String displayName; // 🆕 добавили сюда

    public static UserResponse fromEntity(User user) {
        String avatarUrl = null;
        if (user.getProfile() != null) {
            avatarUrl = user.getProfile().getAvatarUrl();
        }
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null,
                avatarUrl,
                user.getDisplayName() // 🆕 прокинули значение
        );
    }
}