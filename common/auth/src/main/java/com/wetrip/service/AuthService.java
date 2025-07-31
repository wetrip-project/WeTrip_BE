package com.wetrip.service;


import com.wetrip.dto.TokenInfoDto;
import com.wetrip.dto.TokenResponseDto;
import com.wetrip.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final SessionTokenService sessionTokenService;
  private final JwtTokenProvider jwtTokenProvider;
  private final Environment env;

  public boolean isProd() {
    return Arrays.asList(env.getActiveProfiles()).contains("prod");
  }

  // 새로운 AccessToken 발급
  public TokenResponseDto refreshTokens(String refreshToken, HttpServletResponse response) {
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 Refresh Token");
    }

    String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
    Long userIdLong = Long.valueOf(userId);

    String storedRefreshToken = sessionTokenService.getRefreshToken(userIdLong)
        .orElseThrow(() -> new IllegalArgumentException("Refresh Token이 존재하지 않습니다."));

    if (!storedRefreshToken.equals(refreshToken)) {
      throw new IllegalArgumentException("Refresh Token 불일치");
    }

    // 새 AccessToken 발급
    String newAccessToken = jwtTokenProvider.createAccessToken(userId);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

    // Refresh Token 갱신
    sessionTokenService.saveRefreshToken(userIdLong, newRefreshToken);

    // 쿠키 저장
    setTokenCookies(response, newAccessToken, newRefreshToken, isProd());

    return new TokenResponseDto(newAccessToken, newRefreshToken);
  }

  // HttpOnly 쿠키 저장
  public void setTokenCookies(HttpServletResponse response, String accessToken,
      String refreshToken, boolean isProd) {
    String cookieTemplate = "Path=/; HttpOnly; %s";
    // https 접속
    if (isProd) {
      String options = "SameSite=None; Secure";
      response.addHeader("Set-Cookie",
          String.format("accessToken=%s; Max-Age=%d; " + cookieTemplate,
              accessToken, 60 * 60, options));
      response.addHeader("Set-Cookie",
          String.format("refreshToken=%s; Max-Age=%d; " + cookieTemplate,
              refreshToken, 60 * 60 * 24 * 7, options));
    } else { // http 접속
      String options = "SameSite=Lax";
      response.addHeader("Set-Cookie",
          String.format("accessToken=%s; Max-Age=%d; " + cookieTemplate,
              accessToken, 60 * 60, options));
      response.addHeader("Set-Cookie",
          String.format("refreshToken=%s; Max-Age=%d; " + cookieTemplate,
              refreshToken, 60 * 60 * 24 * 7, options));
    }
    log.info("쿠키 저장 완료 : access = {}, refresh = {}", accessToken, refreshToken);
  }

  public void clearTokenCookies(HttpServletResponse response) {
    response.addHeader("Set-Cookie", "accessToken=; Max-Age=0; Path=/; HttpOnly; SameSite=None");
    response.addHeader("Set-Cookie", "refreshToken=; Max-Age=0; Path=/; HttpOnly; SameSite=None");
  }
}
