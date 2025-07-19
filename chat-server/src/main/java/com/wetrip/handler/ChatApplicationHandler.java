package com.wetrip.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wetrip.config.MessageMappingHandler;
import com.wetrip.config.WebSocketSessionManager;
import com.wetrip.dto.SessionInfo;
import com.wetrip.dto.request.ChatMessageRequest;
import com.wetrip.dto.response.ChatResponse;
import com.wetrip.repository.RedisService;
import com.wetrip.service.ChatService;
import com.wetrip.user.repository.UserRepository;
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
  private final ObjectMapper objectMapper;
  private final MessageMappingHandler messageMappingHandler;
  private final ChatService chatService;
  private final WebSocketSessionManager sessionManager;
  private final UserRepository userRepository;

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    try {
      var chatMessage = convertTextMessageToChatMessage(message.getPayload());
      var response = messageMappingHandler.routeMessage(chatMessage, session);
      if (response != null) {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
      }
    } catch (Exception e) {
      log.error("WS got error; session id: {}. message: {}", session.getId(), e.getMessage());
      var response = ChatResponse.failed(e.getMessage());
      session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

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
    var user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      log.error("Can't find user; session id : {}", session.getId());
      session.close();
      return;
    }

    var sessionId = session.getId();

    var sessionInfo = SessionInfo.builder()
        .userId(userId)
        .sessionId(sessionId)
        .username(user.getName())
        .build();

    var userKey = "user:{" + userId + "}";
    var sessionKey = "session:{" + sessionId + "}";
    sessionManager.addSession(session);
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

  private ChatMessageRequest convertTextMessageToChatMessage(String payload) throws Exception {
    return objectMapper.readValue(payload, ChatMessageRequest.class);
  }
}
