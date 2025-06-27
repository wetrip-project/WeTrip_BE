package com.wetrip.post.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.wetrip.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "join_post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JoinPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate travelStartDate;

    @Column(nullable = false)
    private LocalDate travelEndDate;

    @Column(nullable = false)
    private String travelCountry;

    private String travelCity;

    @Column(nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    private Gender recruitmentGender;

    private String recruitmentAge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitmentStatus recruitmentStatus;

    private String estimatedAmount;

//    @Enumerated(EnumType.STRING)
//    private Tag tag;
//
//    @Column(nullable = false)
//    private LocalDate recruitmentStartDate;

    @Column(nullable = false)
    private LocalDate recruitmentEndDate;

//    private String course;

    private Integer recruitmentCount;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long viewCount = 0L;

    // 관계 매핑
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<Thumbnail> thumbnails = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private List<Course> courses = new ArrayList<>();

//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "travelType_id", nullable = false)
//    private TripType travelType;
//
//    @OneToMany(mappedBy = "joinPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<JoinApplication> applications;
//
//    @OneToMany(mappedBy = "joinPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<JoinComment> comments;

    public enum Gender {
        female, male, none
    }

    public enum RecruitmentStatus {
        completed, recruiting
    }

    public enum Tag {
        s20s, s30s, s40s, s50s, female, male
    }
}

