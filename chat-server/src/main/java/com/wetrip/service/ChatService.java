package com.wetrip.service;

import com.wetrip.chat.entity.ChatParticipant;
import com.wetrip.chat.repository.ChatParticipantRepository;
import com.wetrip.dto.RoomInfo;
import com.wetrip.dto.request.ChatMessageRequest;
import com.wetrip.dto.response.ChatResponse;
import com.wetrip.dto.response.ResponseType;
import com.wetrip.repository.RedisService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatParticipantRepository chatParticipantRepository;
  private final MessageService messageService;
  private final RedisService redisService;

  public ChatResponse joinRoom(WebSocketSession session, ChatMessageRequest request) {
    var roomId = UUID.fromString(request.roomId());
    var userId = Long.parseLong(request.userId());
    var chatParticipant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, userId);

    cachedJoinRoom(roomId, userId);
    if (chatParticipant == null) {
      chatParticipant = ChatParticipant.builder()
          .chatRoomId(roomId)
          .userId(userId)
          .participationDate(LocalDateTime.now())
          .build();
      chatParticipant = chatParticipantRepository.saveAndFlush(chatParticipant);
      messageService.sendJoinMessage(roomId);

    }

    return ChatResponse.success(ResponseType.RESPONSE, "Successfully joined the room");
  }

  private void cachedJoinRoom(UUID roomId, Long userId) {
    var key = "room:{" + roomId + "}";
    var roomInfo = redisService.getObject(key, RoomInfo.class);
    if (roomInfo != null) {
      roomInfo.addUser(userId);
      redisService.setObject(key, roomInfo);
      return;
    }

    var users = new ArrayList<Long>();
    users.add(userId);
    roomInfo = RoomInfo.builder()
        .users(users)
        .build();
    redisService.setObject(key, roomInfo);
  }
}
