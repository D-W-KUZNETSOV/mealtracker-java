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
        Path absolutePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        String location = absolutePath.toUri().toString();  // "file:///abs/path/"

        log.info("Раздача картинок: /images/** → {}", location);

        registry.addResourceHandler("/images/**")
                .addResourceLocations(location);
    }
}
