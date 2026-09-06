package com.e.mealtracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserContextService {

    /**
     * Получает имя текущего авторизованного пользователя
     * @return имя пользователя или null, если пользователь не авторизован
     */
    public String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("No authenticated user found");
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            } else {
                log.warn("Unexpected principal type: {}", principal.getClass().getName());
                return principal.toString();
            }
        } catch (Exception e) {
            log.error("Error retrieving current username", e);
            return null;
        }
    }

    /**
     * Проверяет, авторизован ли пользователь
     */
    public boolean isUserAuthenticated() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null && auth.isAuthenticated() &&
                    !"anonymousUser".equals(auth.getPrincipal());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Получает текущего пользователя или выбрасывает исключение
     */
    public String getRequiredUsername() {
        String username = getCurrentUsername();
        if (username == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        return username;
    }
}
