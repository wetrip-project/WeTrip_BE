package com.wetrip.controller;

import com.wetrip.dto.RefreshTokenRequestDto;
import com.wetrip.dto.TokenInfoDto;
import com.wetrip.dto.TokenResponseDto;
import com.wetrip.service.AuthService;
import com.wetrip.service.SessionTokenService;
import com.wetrip.utils.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;
  private final SessionTokenService sessionTokenService;


  // 리프레시 토큰 발급
  @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request,
      HttpServletResponse response) {
    authService.refreshTokens(request.getRefreshToken(), response);
    return ResponseEntity.ok(Map.of("message", "토큰이 갱신되었습니다."));
  }

  // 로그아웃
  @Operation(summary = "로그아웃")
  @PostMapping("/logout")
  public ResponseEntity<?> logout(@AuthenticationPrincipal String userId,
      HttpServletResponse response) {
    log.info("로그아웃 요청: userId={}", userId);
    if (userId != null) {
      sessionTokenService.deleteRefreshToken(Long.valueOf(userId));
    }
    authService.clearTokenCookies(response);
    return ResponseEntity.ok(Map.of("message", "로그아웃이 완료되었습니다"));
  }
}
