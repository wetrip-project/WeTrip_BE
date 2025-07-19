package com.wetrip.chat.repository;

import com.wetrip.chat.entity.ChatRoom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
}
