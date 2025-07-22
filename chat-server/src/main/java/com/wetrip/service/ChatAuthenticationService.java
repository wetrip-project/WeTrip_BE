package com.wetrip.service;

import com.wetrip.chat.repository.ChatParticipantRepository;
import com.wetrip.chat.repository.ChatRoomRepository;
import com.wetrip.dto.request.ChatMessageRequest;
import com.wetrip.post.repository.JoinHistoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAuthenticationService {

  private final JoinHistoryRepository joinHistoryRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatParticipantRepository chatParticipantRepository;

  public boolean roomValidation(ChatMessageRequest request) {
    /* TODO(haechan.yoo): JoinHistory 로직 설계 이후 히스토리 검사 */
    var roomId = UUID.fromString(request.roomId());
    var chatRoom = chatRoomRepository.findById(roomId).orElse(null);

    if (chatRoom == null) {
      log.error("Room does not exist; roomId: {}", roomId);
      return false;
    }

    var participantCount = chatParticipantRepository.countByChatRoomId(roomId);
    if (chatRoom.getLimitNumber() <= participantCount) {
      log.error("Participant limit exceeded; roomId: {}", roomId);
      return false;
    }

    return true;
  }

}
