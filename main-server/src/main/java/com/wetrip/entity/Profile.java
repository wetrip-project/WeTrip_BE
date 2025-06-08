package com.wetrip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    private Long id;

    @Column(nullable = false)
    private byte[] location; // 위치

    @Column(nullable = false)
    @Builder.Default
    private Long mannerTemperature = 75L; // 매너온도

    @Column(nullable = false)
    @Builder.Default
    private Long followerCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long blockedByCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long reportedByCount = 0L;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}