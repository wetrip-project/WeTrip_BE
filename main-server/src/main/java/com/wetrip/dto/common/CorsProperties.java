package com.wetrip.dto.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
