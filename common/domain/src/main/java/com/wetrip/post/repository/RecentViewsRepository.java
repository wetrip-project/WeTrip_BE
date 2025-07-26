package com.wetrip.post.repository;


import com.wetrip.post.entity.RecentViews;
import com.wetrip.post.enums.PostType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentViewsRepository extends JpaRepository<RecentViews, Long> {

  List<RecentViews> findTop30ByUserIdAndPostTypeOrderByViewedAtDesc(Long userId,
      PostType postType); // 최근 본 글 목록 30개를 최신순으로 조회

  RecentViews findByUserIdAndPostIdAndPostType(Long userId, Long postId,
      PostType postType); // 중복 저장 방지 - 동일한 글을 조회하면 새로 저장 X
}
