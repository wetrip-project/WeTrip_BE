package com.wetrip.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Wetrip API 명세서")
                .version("v1.0.0")
                .description("""
                    WeTrip의 백엔드 REST API 문서입니다.

                    ## 인증 방식
                    - Bearer Token (JWT) 방식을 사용합니다.
                    - Authorization 헤더에 'Bearer {토큰}' 형식으로 전달해주세요.

                    ## API 버전
                    - 현재 버전: v1.0.0
                    """)
              .contact(new Contact()
                    .name("WeTrip Development Team")))
            .servers(List.of(
                new Server().url("https://dev-wetrip.shop").description("운영 서버"),
                new Server().url("http://localhost:8080").description("로컬 개발 서버")
            ))
            .components(new Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT 토큰을 입력하세요 (Bearer 접두사 제외)")))
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }
}