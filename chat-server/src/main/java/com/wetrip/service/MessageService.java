package com.wetrip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wetrip.chat.entity.ChatMessage;
import com.wetrip.chat.repository.ChatMessageRepository;
import com.wetrip.config.WebSocketSessionManager;
import com.wetrip.dto.RoomInfo;
import com.wetrip.dto.SessionInfo;
import com.wetrip.dto.request.ChatMessageRequest;
import com.wetrip.dto.response.ChatResponse;
import com.wetrip.dto.response.ResponseType;
import com.wetrip.repository.RedisService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
public class MessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final WebSocketSessionManager sessionManager;
  private final TaskExecutor taskExecutor;
  private final RedisService redisService;
  private final ObjectMapper objectMapper;

  public MessageService(ChatMessageRepository chatMessageRepository,
      WebSocketSessionManager sessionManager,
      @Qualifier("virtualThreadTaskExecutor") TaskExecutor taskExecutor,
      RedisService redisService, ObjectMapper objectMapper) {
    this.chatMessageRepository = chatMessageRepository;
    this.sessionManager = sessionManager;
    this.taskExecutor = taskExecutor;
    this.redisService = redisService;
    this.objectMapper = objectMapper;
  }

  public void sendJoinMessage(UUID roomId) {
    var key = "room:{" + roomId + "}";
    final var roomInfo = redisService.getObject(key, RoomInfo.class);
    List<Long> users = roomInfo.getUsers();

    users.forEach(user -> {
      CompletableFuture.runAsync(() -> {
        String userKey = "user:{" + user + "}";
        var sessionInfo = redisService.getObject(userKey, SessionInfo.class);
        if (sessionInfo != null) {
          var session = sessionManager.get(sessionInfo.sessionId());
          var message = ChatResponse.success(ResponseType.JOIN_ROOM, sessionInfo.username() + "님이 입장하셨습니다.");
          sendMessage(session, message);
        }
      }, taskExecutor);
    });
  }

  public void sendMessage(WebSocketSession session, ChatResponse response) {
    if (session == null || !session.isOpen()) {
      log.error("Session is null or closed");
      return;
    }
    try {
      var message = objectMapper.writeValueAsString(response);
      session.sendMessage(new TextMessage(message));
    } catch (JsonProcessingException e) {
      log.error("Can't serialize response : {}", response, e);
    } catch (IOException e) {
      log.error("Can't send message : {}", response, e);
    }
  }
}
