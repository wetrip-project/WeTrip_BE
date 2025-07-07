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

    //  @Override
//  protected void doFilterInternal(HttpServletRequest request,
//      HttpServletResponse response,
//      FilterChain filterChain)
//      throws ServletException, IOException {
//    String bearer = request.getHeader("Authorization"); // 요청 헤더에서 Authorization 값을 가져옴
//    if (isMock()) {
//      var authenticationToken = new UsernamePasswordAuthenticationToken(bearer, null, null);
//      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//      filterChain.doFilter(request, response); // 다음 필터로 요청 전달
//    }
//
//    if (bearer != null && bearer.startsWith("Bearer ")) {  // Authorization 값이 "Bearer {토큰}" 형식인지 확인
//      String token = bearer.substring(7); // "Bearer " 부분 제거하여 실제 토큰만 추출
//      if (jwtTokenProvider.validateToken(token)) { // 토큰 유효성 검사
//        String userId = jwtTokenProvider.getUserIdFromToken(token); // 유효한 토큰이면 사용자 ID 추출
//        UsernamePasswordAuthenticationToken authToken =  // 사용자 ID를 기반으로 인증 객체 생성 (권한은 생략된 상태)
//            new UsernamePasswordAuthenticationToken(userId, null, null);
//
//        authToken.setDetails(
//            new WebAuthenticationDetailsSource().buildDetails(request)); // 요청 정보로부터 인증 디테일 설정
//        SecurityContextHolder.getContext().setAuthentication(
//            authToken); // SecurityContext에 인증 정보 등록 → 이후 컨트롤러에서 @AuthenticationPrincipal로 접근 가능
//      }
//    }
//    filterChain.doFilter(request, response); // 다음 필터로 요청 전달
//  }
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
                        tokenInfo.get().updateLastAccessedAt();
                        sessionTokenService.saveTokenInfo(userIdFromRedis.get(), tokenInfo.get());
                        sessionTokenService.extendTokenExpiration(userIdFromRedis.get(), token);

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
