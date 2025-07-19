package com.wetrip.chat.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @Column(nullable = false, length = 36)
    private UUID id;

    private String roomName;

    @Column(nullable = false)
    private String roomType;

    private Integer limitNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

//    @Column(nullable = false, length = 36)
//    private String roomStatus;
//
//    private LocalDateTime deactivatedAt;

    @PrePersist
    private void prePersist() {
        if (id == null)
            this.id = UUID.randomUUID();
    }
}