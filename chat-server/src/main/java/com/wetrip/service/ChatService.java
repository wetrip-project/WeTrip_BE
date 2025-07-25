package com.wetrip.service;

import com.wetrip.chat.entity.ChatParticipant;
import com.wetrip.chat.repository.ChatParticipantRepository;
import com.wetrip.dto.RoomInfo;
import com.wetrip.dto.request.ChatMessageRequest;
import com.wetrip.dto.response.ChatResponse;
import com.wetrip.dto.response.ResponseType;
import com.wetrip.repository.RedisService;
import com.wetrip.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatParticipantRepository chatParticipantRepository;
  private final MessageService messageService;
  private final RedisService redisService;
  private final UserRepository userRepository;

  @Transactional
  public ChatResponse joinRoom(WebSocketSession session, ChatMessageRequest request) {
    var roomId = UUID.fromString(request.roomId());
    var userId = Long.parseLong(request.userId());
    var user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      return ChatResponse.failed("User not found; userId = " + userId);
    }

    var chatParticipant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, userId);
    cachedJoinRoom(roomId, userId);

    if (chatParticipant == null) {
      chatParticipant = ChatParticipant.builder()
          .chatRoomId(roomId)
          .userId(userId)
          .participationDate(LocalDateTime.now())
          .build();
      chatParticipant = chatParticipantRepository.saveAndFlush(chatParticipant);
      messageService.sendJoinMessage(user, roomId);

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

    var users = new HashSet<Long>();
    users.add(userId);
    roomInfo = RoomInfo.builder()
        .users(users)
        .build();
    redisService.setObject(key, roomInfo);
  }
}
