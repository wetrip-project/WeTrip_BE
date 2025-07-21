package com.wetrip.post.repository;


import com.wetrip.post.entity.JoinPost;
import com.wetrip.post.enums.RecruitmentStatus;
import com.wetrip.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinPostRepository extends JpaRepository<JoinPost, Long> {

  // 모집 상태(모집 완료, 모집 중) 별로 글을 최신 순으로 조회
  List<JoinPost> findByAuthorAndRecruitmentStatus(User user,
      RecruitmentStatus recruitmentStatus);
}
