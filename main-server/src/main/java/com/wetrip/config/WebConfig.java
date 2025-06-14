package com.wetrip.config;

import com.wetrip.dto.common.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(corsProperties.addMapping())
                .allowedOrigins(corsProperties.origins())
                .allowedMethods(corsProperties.methods())
                .allowedHeaders(corsProperties.allowedHeaders())
                .exposedHeaders(corsProperties.exposedHeaders())
                .allowCredentials(corsProperties.credential())
                .maxAge(corsProperties.maxAge());
    }
}
