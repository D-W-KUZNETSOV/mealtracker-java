package com.e.mealtracker.config;

import com.e.mealtracker.security.JwtAuthenticationFilter;
import jakarta.servlet.annotation.WebServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.web.servlet.ServletRegistrationBean;


import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Какие домены разрешены
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",   // React / Next.js
                "http://localhost:5173",   // Vite (React/Vue)
                "http://localhost:4200",   // Angular
                "http://localhost:8080"    // Сам бэкенд (для Swagger)
        ));

        // ✅ Какие HTTP методы разрешены
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // ✅ Какие заголовки разрешены
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With"
        ));

        // ✅ Разрешены ли куки и авторизация
        configuration.setAllowCredentials(true);

        // ✅ Как долго кэшировать настройки CORS (в секундах)
        configuration.setMaxAge(3600L);

        // ✅ Применяем настройки ко всем эндпоинтам
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public ServletRegistrationBean<org.h2.server.web.JakartaWebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<org.h2.server.web.JakartaWebServlet> bean =
                new ServletRegistrationBean<>(new org.h2.server.web.JakartaWebServlet(), "/h2-console/*");
        bean.addInitParameter("webAllowOthers", "true");
        return bean;
    }






}