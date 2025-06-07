package com.wetrip.entity;

import jakarta.persistence.*;
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
    private String id;

    private String roomName;

    @Column(nullable = false)
    private String roomType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 36)
    private String roomStatus;

    private LocalDateTime deactivatedAt;
}