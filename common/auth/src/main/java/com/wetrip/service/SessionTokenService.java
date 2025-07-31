package com.wetrip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wetrip.dto.TokenInfoDto;
import com.wetrip.utils.JwtTokenProvider;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionTokenService {

  private final StringRedisTemplate redisTemplate; // 자동 주입(StringRedisTemplate로 교체)

  private static final String REFRESH_TOKEN_PREFIX = "refresh_tokens:";
  private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(1); // 24시간

  // Refresh Token만 저장
  public void saveRefreshToken(Long userId, String refreshToken) {
    String key = REFRESH_TOKEN_PREFIX + userId;
    redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRATION);
    log.info("Refresh Token 저장 완료: userId={}", userId);
  }

  // Refresh Token 조회
  public Optional<String> getRefreshToken(Long userId) {
    String key = REFRESH_TOKEN_PREFIX + userId;
    return Optional.ofNullable(redisTemplate.opsForValue().get(key));
  }

  // Refresh Token 삭제
  public void deleteRefreshToken(Long userId) {
    String key = REFRESH_TOKEN_PREFIX + userId;
    try {
      redisTemplate.delete(key);
      log.info("Refresh Token 삭제 완료: userId={}", userId);
    } catch (Exception e) {
      log.warn("Refresh Token 삭제 중 오류: key={}", key, e);
    }
  }
}
