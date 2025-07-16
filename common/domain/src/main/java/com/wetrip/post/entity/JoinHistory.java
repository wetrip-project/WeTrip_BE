package com.wetrip.post.entity;

import com.wetrip.post.enums.JoinStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinStatus status;

    @Column(nullable = false)
    private LocalDateTime confirmedDate;

    @Column(nullable = false)
    private LocalDateTime completedDate;

    @Column(nullable = false)
    private Boolean isReviewWritten;

    private Byte joinRating;

    // 관계 매핑
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id", insertable = false, updatable = false)
//    private JoinPost joinPost;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private User user;

}