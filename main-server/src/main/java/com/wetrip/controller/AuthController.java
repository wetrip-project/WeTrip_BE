package com.wetrip.controller;

import com.wetrip.dto.RefreshTokenRequestDto;
import com.wetrip.dto.TokenInfoDto;
import com.wetrip.dto.TokenResponseDto;
import com.wetrip.service.AuthService;
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

  private final AuthService authService;

  // 리프레시 토큰 발급
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request) {
    TokenResponseDto response = authService.refreshTokens(request.getRefreshToken());
    return ResponseEntity.ok(response);
  }
}
