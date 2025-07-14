package com.wetrip.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wetrip.user.entity.User;
import com.wetrip.user.repository.UserRepository;
import com.wetrip.utils.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @Value("${frontend.base-url}")
  private String frontendBaseUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    System.out.println("OAuth2User Attributes: " + oAuth2User.getAttributes());

    OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
    String registrationId = authToken.getAuthorizedClientRegistrationId();

    Long userId = Optional.ofNullable(oAuth2User.getAttribute("userId"))
        .map(id -> Long.parseLong(id.toString()))
        .orElseThrow(() -> {
          return new IllegalStateException("userId가 OAuth2User에 없습니다.");
        });

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalStateException("해당 userId로 사용자를 찾을 수 없습니다."));

    // 플랫폼별 ID 추출
    String socialId = switch (registrationId) {
      case "kakao" -> oAuth2User.getAttribute("id").toString();      // Long 형태
      case "google" -> oAuth2User.getAttribute("sub").toString();
      case "naver" -> {
        Map<String, Object> responseMap = (Map<String, Object>) oAuth2User.getAttributes();
        yield responseMap.get("id")
            .toString(); // NaverOAuth2UserService에서 이미 평탄화한 경우라면 oAuth2User.getAttribute("id")로 대체 가능
      }// String 형태
      default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
    };

    // DB에서 User 정보 조회
    //User user = userRepository.findBySocialId(socialId).orElseThrow();

    // 토큰 생성
    String accessToken = jwtTokenProvider.createAccessToken(user.getId().toString());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId().toString());

    String callbackPath = "/auth/" + registrationId + "/callback";
    String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
    String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

    String frontendRedirectUrl = frontendBaseUrl + callbackPath
        + "?accessToken=" + encodedAccessToken
        + "&refreshToken=" + encodedRefreshToken;
    response.sendRedirect(frontendRedirectUrl);
  }
}