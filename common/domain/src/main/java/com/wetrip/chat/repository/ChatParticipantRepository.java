package com.wetrip.chat.repository;


import com.wetrip.chat.entity.ChatParticipant;
import com.wetrip.chat.entity.ChatRoom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

  int countByChatRoomId(UUID chatRoomId);
  ChatParticipant findByChatRoomIdAndUserId(UUID chatRoomId, Long userId);
}
