package com.wetrip.controller;

import com.wetrip.config.annotations.ChatMessageMapping;
import com.wetrip.dto.request.ChatMessageRequest;
import com.wetrip.dto.response.ChatResponse;
import com.wetrip.service.ChatAuthenticationService;
import com.wetrip.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final ChatAuthenticationService authService;

  @ChatMessageMapping(type = "JOIN_ROOM")
  public ChatResponse joinRoom(WebSocketSession session, ChatMessageRequest request) {
    /*
    TODO: 1. 채팅방 유효검삭
      2. 채팅방 입장
      2-1. 기존 유저라면 Session만 저장
      2-2. 기존 유저가 아니라면 채팅방 입장 설정
      3. 채팅방 입장완료 메시지 남기기
     */
    boolean isValid = authService.roomValidation(request);
    if (!isValid) {
      return ChatResponse.failed("Can't join room. User doesn't have permission");
    }

    return chatService.joinRoom(session, request);
  }
}
