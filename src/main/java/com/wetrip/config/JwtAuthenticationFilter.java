package com.wetrip.config;

import com.wetrip.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //JWT가 포함된 요청의 토큰을 검증하고 인증 객체를 등록하는 필터

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String bearer = request.getHeader("Authorization"); // 요청 헤더에서 Authorization 값을 가져옴

        if (bearer != null && bearer.startsWith("Bearer ")) {  // Authorization 값이 "Bearer {토큰}" 형식인지 확인
            String token = bearer.substring(7); // "Bearer " 부분 제거하여 실제 토큰만 추출
            if (jwtTokenProvider.validateToken(token)) { // 토큰 유효성 검사
                String userId = jwtTokenProvider.getUserIdFromToken(token); // 유효한 토큰이면 사용자 ID 추출
                UsernamePasswordAuthenticationToken authToken =  // 사용자 ID를 기반으로 인증 객체 생성 (권한은 생략된 상태)
                        new UsernamePasswordAuthenticationToken(userId, null, null);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 요청 정보로부터 인증 디테일 설정
                SecurityContextHolder.getContext().setAuthentication(authToken); // SecurityContext에 인증 정보 등록 → 이후 컨트롤러에서 @AuthenticationPrincipal로 접근 가능
            }
        }
        filterChain.doFilter(request, response); // 다음 필터로 요청 전달
    }
}
