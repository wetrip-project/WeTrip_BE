package com.wetrip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wetrip.dto.TokenInfoDto;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionTokenService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  private static final String TOKEN_PREFIX = "token:";
  private static final String USER_TOKEN_PREFIX = "user_tokens:";
  private static final Duration TOKEN_EXPIRATION = Duration.ofDays(1); // 24시간

  // 토큰 정보를 Redis에 저장
  public void saveTokenInfo(Long userId, TokenInfoDto tokenInfo) {
    try {
      String userTokenKey = USER_TOKEN_PREFIX + userId;
      String tokenJson = objectMapper.writeValueAsString(tokenInfo);

      // 사용자별 토큰 정보 저장
      redisTemplate.opsForValue().set(userTokenKey, tokenJson, TOKEN_EXPIRATION);

      // Access Token으로 userId 조회를 위한 매핑 저장
      String accessTokenKey = TOKEN_PREFIX + tokenInfo.getAccessToken();
      redisTemplate.opsForValue().set(accessTokenKey, userId.toString(), TOKEN_EXPIRATION);

      log.info("토큰 정보가 Redis에 저장되었습니다. userId: {}", userId);
      log.info("Redis에 저장하는 AccessToken = {}", tokenInfo.getAccessToken());
    } catch (JsonProcessingException e) {
      log.error("토큰 정보 직렬화 중 오류 발생", e);
      throw new RuntimeException("토큰 저장 실패", e);
    }
  }

  // 사용자 ID로 토큰 정보 조회
  public Optional<TokenInfoDto> getTokenInfoByUserId(Long userId) {
    try {
      String userTokenKey = USER_TOKEN_PREFIX + userId;
      String tokenJson = (String) redisTemplate.opsForValue().get(userTokenKey);

      if (tokenJson == null) {
        return Optional.empty();
      }

      TokenInfoDto tokenInfo = objectMapper.readValue(tokenJson, TokenInfoDto.class);
      return Optional.of(tokenInfo);
    } catch (JsonProcessingException e) {
      log.error("토큰 정보 역질렬화 중 오류 발생");
      return Optional.empty();
    }
  }

  // 액세스 토큰으로 사용자 ID 조회
  public Optional<Long> getUserIdByAccessToken(String accessToken) {
    String accessTokenKey = TOKEN_PREFIX + accessToken;
    String userIdStr = (String) redisTemplate.opsForValue().get(accessTokenKey);

    if (userIdStr == null) {
      return Optional.empty();
    }

    try {
      return Optional.of(Long.parseLong(userIdStr));
    } catch (NumberFormatException e) {
      log.error("사용자 ID 파싱 오류: {}", userIdStr);
      return Optional.empty();
    }
  }

  // 토큰 정보 삭제
  public void deleteTokenInfo(Long userId, String accessToken) {
    String userTokenKey = USER_TOKEN_PREFIX + userId;
    String accessTokenKey = TOKEN_PREFIX + accessToken;

    redisTemplate.delete(userTokenKey);
    redisTemplate.delete(accessTokenKey);

    log.info("토큰 정보가 삭제되었습니다. userId: {}", userId);
  }

  // 토큰 만료 시간 연장
  public void extendTokenExpiration(Long userId, String accessToken) {
    String userTokenKey = USER_TOKEN_PREFIX + userId;
    String accessTokenKey = TOKEN_PREFIX + accessToken;

    redisTemplate.expire(userTokenKey, TOKEN_EXPIRATION);
    redisTemplate.expire(accessTokenKey, TOKEN_EXPIRATION);
  }

  // 토큰 존재 여부 확인
  public boolean existsToken(String accessToken) {
    String accessTokenKey = TOKEN_PREFIX + accessToken;
    return Boolean.TRUE.equals(redisTemplate.hasKey(accessTokenKey));
  }
}
