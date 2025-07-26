package com.wetrip.post.entity;

import com.wetrip.post.enums.PostType;
import com.wetrip.user.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinBookmark extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostType postType;

//    @Column(nullable = false)
//    private LocalDateTime bookmarkedAt;

/*  @Column(nullable = false) // JoinPost에서 관리
  private Integer bookmarkCount = 0;*/

  @Column(nullable = false)
  private Boolean state = Boolean.TRUE;

  // 관계 매핑
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private User user;
}