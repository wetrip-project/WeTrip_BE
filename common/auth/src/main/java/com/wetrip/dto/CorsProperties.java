package com.wetrip.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cors")
public record CorsProperties(
        String addMapping,
        String[] origins,
        String[] methods,
        String[] allowedHeaders,
        String[] exposedHeaders,
        Boolean credential,
        Long maxAge
) {
}
