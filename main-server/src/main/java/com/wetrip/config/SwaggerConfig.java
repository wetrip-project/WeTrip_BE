package com.wetrip.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                .version("v1")
                .description("WeTrip의 백엔드 REST API 문서입니다."))
            .servers(List.of(
                new Server().url("https://dev-wetrip.shop").description("운영 서버"),
                new Server().url("http://localhost:8080").description("로컬 개발 서버")
            ));
    }
}
