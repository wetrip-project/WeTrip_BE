package com.wetrip.chat.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID chatRoomId;

    @Column(nullable = false)
    private Long userId;

    private Long messageId;

    @Column(nullable = false)
    private LocalDateTime participationDate;

    private LocalDateTime exitDate;
}


