package com.wetrip.utils;


import com.wetrip.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;
  private Key key;

  @PostConstruct
  protected void init() {
    this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
  }

  // Access Token 생성
  public String createAccessToken(String userId) {
    return createToken(userId, jwtProperties.accessTokenExpiration());
  }

  // Refresh Token 생성
  public String createRefreshToken(String userId) {
    return createToken(userId, jwtProperties.refreshTokenExpiration());
  }

  // 토큰 생성 공통 로직
  private String createToken(String userId, long validityMillis) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + validityMillis);

    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // 토큰에서 사용자 ID 추출
  public String getUserIdFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

  // JWT Claims 파싱
  public Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // 토큰 만료일 가져오기
  public Date getExpirationDate(String token) {
    Claims claims = parseClaims(token);
    return claims.getExpiration();
  }
}
