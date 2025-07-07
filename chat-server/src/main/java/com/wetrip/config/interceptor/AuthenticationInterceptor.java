package com.wetrip.config.interceptor;

import com.wetrip.auth.SimpleJwtValidator;
import com.wetrip.repository.RedisService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandshakeInterceptor {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final SimpleJwtValidator jwtValidator;

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

    var bearerToken = request.getHeaders().getFirst(AUTHORIZATION_HEADER);

    if (StringUtils.startsWithIgnoreCase(bearerToken, BEARER_PREFIX)) {
      var token = bearerToken.substring(7);
      if (jwtValidator.validateToken(token)) {
        var userId = jwtValidator.getUserIdFromToken(token);

        // TODO(haechan) : room의 권한 확인 체크 로직 필요
        attributes.put("userId", userId);
        attributes.put("isAuthenticated", true);
        return true;
      }
      return false;
    }
    return false;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {

  }
}
