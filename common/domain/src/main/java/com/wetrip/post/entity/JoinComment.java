package com.wetrip.post.entity;

import com.wetrip.user.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "join_comment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinComment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "author_id", nullable = false)
  private Long authorId;

  @Column(nullable = false)
  private String content;

//  @Column(nullable = false)
//  private LocalDateTime commentTime;
//
//  private LocalDateTime updatedTime;

  // 관계 매핑
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id", insertable = false, updatable = false)
//    private JoinPost joinPost;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "author_id", insertable = false, updatable = false)
//    private User author;
}