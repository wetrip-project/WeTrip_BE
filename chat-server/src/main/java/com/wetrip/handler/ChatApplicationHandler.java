package com.wetrip.handler;

import com.wetrip.auth.SimpleJwtValidator;
import com.wetrip.dto.SessionInfo;
import com.wetrip.repository.RedisService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatApplicationHandler extends TextWebSocketHandler {

  private final RedisService redisService;

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    super.handleTextMessage(session, message);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    var isAuthenticated = (Boolean) session.getAttributes().get("isAuthenticated");

    if (!isAuthenticated) {
      log.error("Session {} is not authenticated", session.getId());
      session.close();
      return;
    }

    var userId = Long.parseLong(session.getAttributes().get("userId").toString());
    var sessionId = session.getId();

    var sessionInfo = SessionInfo.builder()
        .userId(userId)
        .sessionId(sessionId)
        .build();

    var userKey = "user:{" + userId + "}";
    var sessionKey = "session:{" + sessionId + "}";
    redisService.setObject(sessionKey, sessionInfo, Duration.ofHours(1));
    redisService.setObject(userKey, sessionInfo, Duration.ofHours(1));

    super.afterConnectionEstablished(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    super.afterConnectionClosed(session, status);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    super.handleTransportError(session, exception);
  }
}
