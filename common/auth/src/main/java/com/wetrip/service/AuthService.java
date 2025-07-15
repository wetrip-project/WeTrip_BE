package com.wetrip.service;


import com.wetrip.dto.TokenInfoDto;
import com.wetrip.dto.TokenResponseDto;
import com.wetrip.utils.JwtTokenProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final SessionTokenService sessionTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  // 새로운 액세스 토큰 및 리프레시 토큰 반환
  public TokenResponseDto refreshTokens(String refreshToken) {
    log.info("요청받은 refreshToken = {}", refreshToken);

    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 refresh token입니다");
    }

    String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
    Long userIdLong = Long.valueOf(userId);

    Optional<TokenInfoDto> existingTokenInfo = sessionTokenService.getTokenInfoByUserId(userIdLong);
    if (existingTokenInfo.isEmpty()) {
      throw new IllegalArgumentException("토큰 정보를 찾을 수 없습니다.");
    }

    TokenInfoDto tokenInfoDto = existingTokenInfo.get();

    String newAccessToken = jwtTokenProvider.createAccessToken(userId);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

    tokenInfoDto.setAccessToken(newAccessToken);
    tokenInfoDto.setRefreshToken(newRefreshToken);

    sessionTokenService.saveTokenInfo(userIdLong, tokenInfoDto);

    return new TokenResponseDto(newAccessToken, newRefreshToken);
  }
}
