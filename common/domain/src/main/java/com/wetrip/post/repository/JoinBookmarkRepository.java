package com.wetrip.post.repository;


import com.wetrip.post.entity.JoinBookmark;
import com.wetrip.post.enums.PostType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinBookmarkRepository extends JpaRepository<JoinBookmark, Long> {

  List<JoinBookmark> findByUserIdAndPostTypeOrderByIdDesc(Long userId,
      PostType postType); // 스크랩한 글 목록을 최신순으로 가져옴

  boolean existsByUserIdAndPostIdAndPostType(Long userId, Long postId,
      PostType postType); // 스크랩 여부 확인

  void deleteByUserIdAndPostIdAndPostType(Long userId, Long postId, PostType postType); // 스크랩 해제
}
