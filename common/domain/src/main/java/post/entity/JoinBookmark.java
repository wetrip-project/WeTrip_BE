package post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookmarkType bookmarkType;

    @Column(nullable = false)
    private LocalDateTime bookmarkedAt;

    @Column(nullable = false)
    private Integer bookmarkCount = 0;

    @Column(nullable = false)
    private Boolean state = Boolean.TRUE;

    // 관계 매핑
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private User user;

    public enum BookmarkType {
        POST, COMMUNITY
    }
}