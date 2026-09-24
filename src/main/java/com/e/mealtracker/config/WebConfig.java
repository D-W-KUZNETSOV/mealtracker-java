package com.e.mealtracker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Твоя раздача картинок
        Path absolutePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        String location = absolutePath.toUri().toString();
        log.info("Раздача картинок: /images/** → {}", location);
        registry.addResourceHandler("/images/**")
                .addResourceLocations(location);

        // ✅ ВОТ ЭТО ДОБАВЬ: явная регистрация Swagger UI
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    }
}