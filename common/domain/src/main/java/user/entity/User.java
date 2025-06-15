package user.entity;

import community.entity.Community;
import jakarta.persistence.*;
import lombok.*;
import notification.entity.Notification;
import post.entity.JoinPost;
import post.entity.RecentViews;
import review.entity.Review;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 닉네임

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MBTI mbti;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createAt; // 가입일

    @Column(length = 512)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType; // 간편 로그인 타입

    private String socialId; // 소셜 고유 ID

    private LocalDate birthDate;

    @Column(length = 50)
    private String contact; // 연락처

    private String email;

    // 1:1 관계들
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAgreement userAgreement;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Location location;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // 1:N 관계들
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<UserTripType> userTripTypes;
//
//    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Relation> relationsFrom;
//
//    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Relation> relationsTo;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Notification> notifications;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<RecentViews> recentViews;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Tokens> tokens;
//
//    // 작성한 게시글들
//    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<JoinPost> joinPosts;
//
//    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Community> communities;
//
//    // 리뷰 관련
//    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Review> writtenReviews;
//
//    @OneToMany(mappedBy = "reviewee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Review> receivedReviews;

    @PrePersist
    protected void onCreate() { // 가입일 자동 저장
        this.createAt = new Date();
    }

    public enum Gender {
        female, male, none
    }

    public enum MBTI {
        ISTJ, ISTP, ISFJ, ISFP, INTJ, INTP, INFJ, INFP,
        ESTJ, ESTP, ESFJ, ESFP, ENTJ, ENTP, ENFJ, ENFP, none
    }

    public enum LoginType {
        KAKAO, GOOGLE, NAVER
    }
}