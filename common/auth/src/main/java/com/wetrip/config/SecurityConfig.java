package com.wetrip.config;

import com.wetrip.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  // 생성자 주입으로 받음 → @Service로 등록된 CustomOAuth2UserService 자동 주입
  private final CustomOAuth2UserService customOAuth2UserService; // 사용자 정의 OAuth2 사용자 정보 서비스 (ex: 카카오 로그인 처리 로직)
  private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler; // 로그인 성공 시 추가 동작을 정의하는 핸들러
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final Environment env;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // CSRF 보호 기능 비활성화 (개발 중 혹은 API 서버일 경우 비활성화함)
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // H2 콘솔 확인용
        .authorizeHttpRequests(auth -> auth // URL 경로별 접근 권한 설정
            .requestMatchers("/", "/login/**", "/css/**", "/js/**", "/h2-console/**")
            .permitAll() // 루트(/), 로그인 관련 경로, 정적 자원(css/js), H2 콘솔은 인증 없이 접근 허용
            .anyRequest().authenticated() // 위를 제외한 모든 요청은 인증 필요
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    if (!isMock()) {
      http.oauth2Login(oauth -> oauth // OAuth2 로그인 설정
          .loginPage("/login") // 사용자 지정 로그인 페이지 경로 (ex: /login 요청 시 로그인 폼 표시)
          .defaultSuccessUrl("/home", true) // 로그인 성공 시 이동할 기본 URL (true면 무조건 이 URL로 이동)
          .userInfoEndpoint(userInfo -> userInfo // 사용자 정보 가져올 서비스 설정
              .userService(customOAuth2UserService) // 카카오 사용자 정보 처리
          )
          .successHandler(customOAuth2SuccessHandler) // 로그인 성공 후 추가 처리를 위한 핸들러 (ex: 토큰 저장, 리디렉션 등)
      );
    }

    return http.build(); // 최종적으로 보안 필터 체인을 반환
  }

  private boolean isMock() {
    var profiles = Arrays.asList(env.getActiveProfiles());
    return profiles.contains("mock");
  }
}
