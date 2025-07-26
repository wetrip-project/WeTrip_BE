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
  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;

  private static final String USER_TOKEN_PREFIX = "user_tokens:";
  private static final Duration TOKEN_EXPIRATION = Duration.ofDays(1); // 24시간

  // 토큰 정보를 Redis에 저장
  public void saveTokenInfo(Long userId, TokenInfoDto tokenInfo) {
    try {

      String hashTag = "{" + userId + "}";

      String userTokenKey = USER_TOKEN_PREFIX + hashTag;
      String tokenJson = objectMapper.writeValueAsString(tokenInfo);

      // 사용자별 토큰 정보 저장
      redisTemplate.opsForValue().set(userTokenKey, tokenJson, TOKEN_EXPIRATION);

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
      String hashTag = "{" + userId + "}";
      String userTokenKey = USER_TOKEN_PREFIX + hashTag;
      String tokenJson = redisTemplate.opsForValue().get(userTokenKey);

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
    try {
      if (!jwtTokenProvider.validateToken(accessToken)) {
        log.debug("유효하지 않은 JWT 토큰입니다.");
        return Optional.empty();
      }

      String userIdString = jwtTokenProvider.getUserIdFromToken(accessToken);
      Long userId = Long.parseLong(userIdString);

      Optional<TokenInfoDto> tokenInfo = getTokenInfoByUserId(userId);

      if (tokenInfo.isPresent() && accessToken.equals(tokenInfo.get().getAccessToken())) {
        log.info("요청 토큰: '{}'", accessToken);
        log.info("Redis 토큰: '{}'", tokenInfo.get().getAccessToken());

        return Optional.of(userId);
      }
      log.debug("Redis에 저장된 토큰과 일치하지 않습니다. userId: {}", userId);
      return Optional.empty();
    } catch (Exception e) {
      log.error("Access Token에서 userId 추출 중 오류 발생", e);
      return Optional.empty();
    }
  }

  // 토큰 정보 삭제
  public void deleteTokenInfo(Long userId) {
    String hashTag = "{" + userId + "}";
    String userTokenKey = USER_TOKEN_PREFIX + hashTag;

    redisTemplate.unlink(userTokenKey);

    log.info("토큰 정보가 삭제되었습니다. userId: {}", userId);
  }

  // 토큰 만료 시간 연장
  public void extendTokenExpiration(Long userId) {
    String hashTag = "{" + userId + "}";
    String userTokenKey = USER_TOKEN_PREFIX + hashTag;

    redisTemplate.expire(userTokenKey, TOKEN_EXPIRATION);

    log.info("토큰 만료 시간이 연장되었습니다. userId: {}", userId);
  }

  // 토큰 존재 여부 확인
  public boolean existsToken(String accessToken) {
    try {
      if (!jwtTokenProvider.validateToken(accessToken)) {
        return false;
      }

      String userIdString = jwtTokenProvider.getUserIdFromToken(accessToken);
      Long userId = Long.parseLong(userIdString);

      Optional<TokenInfoDto> tokenInfo = getTokenInfoByUserId(userId);

      return tokenInfo.isPresent() &&
          accessToken.equals(tokenInfo.get().getAccessToken());
    } catch (Exception e) {
      log.error("토큰 존재 여부 확인 중 오류 발생", e);
      return false;
    }
  }
}
