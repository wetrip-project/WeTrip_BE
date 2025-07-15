package com.wetrip.config;

import com.wetrip.dto.TokenInfoDto;
import com.wetrip.service.SessionTokenService;
import com.wetrip.utils.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends
    OncePerRequestFilter { //JWT가 포함된 요청의 토큰을 검증하고 인증 객체를 등록하는 필터

  private final JwtTokenProvider jwtTokenProvider;
  private final SessionTokenService sessionTokenService;
  private final Environment env;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = extractTokenFromRequest(request);
      if (token != null && jwtTokenProvider.validateToken(token)) {
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        Optional<Long> userIdFromRedis = sessionTokenService.getUserIdByAccessToken(token);

        if (userIdFromRedis.isPresent() && userIdFromRedis.get().toString().equals(userId)) {
          Optional<TokenInfoDto> tokenInfo = sessionTokenService.getTokenInfoByUserId(
              userIdFromRedis.get());
          if (tokenInfo.isPresent()) {
            sessionTokenService.saveTokenInfo(userIdFromRedis.get(), tokenInfo.get());
            sessionTokenService.extendTokenExpiration(userIdFromRedis.get());

            // 추후 신고 누적에 따른 차단 및 관리자 권한 기능 고려
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("사용자 인증 성공: {}", userId);
          }
        } else {
          log.warn("Redis에 토큰 정보가 없음. token: {}", token.substring(0, 10));
        }
      }
    } catch (Exception e) {
      log.error("JWT 인증 처리 중 오류 발생", e);
    }
    filterChain.doFilter(request, response);
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  public boolean isMock() {
    var profiles = Arrays.asList(env.getActiveProfiles());
    return profiles.contains("mock");
  }
}
