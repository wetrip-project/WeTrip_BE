package com.wetrip.controller;

import com.wetrip.dto.RefreshTokenRequestDto;
import com.wetrip.dto.TokenInfoDto;
import com.wetrip.dto.TokenResponseDto;
import com.wetrip.service.SessionTokenService;
import com.wetrip.utils.JwtTokenProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final SessionTokenService sessionTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  // 리프레시 토큰 발급
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request) {
    try {
      String refreshToken = request.getRefreshToken();
      log.info("요청받은 refreshToken = {}", refreshToken);

      if (!jwtTokenProvider.validateToken(refreshToken)) {
        return ResponseEntity.badRequest().body("유효하지 않은 refresh token입니다");
      }

      String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
      Long userIdLong = Long.valueOf(userId);
      log.info("추출된 userId = {}", userId);

      // 기존 토큰 정보 조회
      Optional<TokenInfoDto> existingTokenInfo = sessionTokenService.getTokenInfoByUserId(
          userIdLong);
      log.info("Redis 조회 결과 = {}", existingTokenInfo.isPresent());

      if (existingTokenInfo.isEmpty()) {
        return ResponseEntity.badRequest().body("토큰 정보를 찾을 수 없습니다.");
      }

      TokenInfoDto tokenInfo = existingTokenInfo.get();

      // 새로운 액세스 토큰 생성
      String newAcessToken = jwtTokenProvider.createAccessToken(userId);

      // 새로운 리프레시 토큰 생성
      String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

      // 토큰 정보 업데이트
      tokenInfo.setAccessToken(newAcessToken);
      tokenInfo.setRefreshToken(newRefreshToken);
      tokenInfo.updateLastAccessedAt();

      // redis에서 저장
      sessionTokenService.saveTokenInfo(userIdLong, tokenInfo);

      return ResponseEntity.ok().body(new TokenResponseDto(newAcessToken, newRefreshToken));
    } catch (Exception e) {
      return ResponseEntity.status(500).body("토큰 갱신 중 오류가 발생했습니다.");
    }
  }
}
