package com.wetrip.service.user;

import com.wetrip.dto.joinpost.JoinPostSummaryDto;
import com.wetrip.post.entity.JoinPost;
import com.wetrip.post.entity.RecentViews;
import com.wetrip.post.entity.Tags;
import com.wetrip.post.enums.PostType;
import com.wetrip.post.enums.RecruitmentStatus;
import com.wetrip.post.enums.TagType;
import com.wetrip.post.repository.JoinPostRepository;
import com.wetrip.post.repository.RecentViewsRepository;
import com.wetrip.post.repository.TagRepository;
import com.wetrip.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final JoinPostRepository joinPostRepository;
  private final TagRepository tagRepository;
  private final RecentViewsRepository recentViewsRepository;

  public List<JoinPostSummaryDto> getMyPostStatus(User user, RecruitmentStatus status) {
    List<JoinPost> posts = joinPostRepository.findByAuthorAndRecruitmentStatus(user, status);
    return posts.stream().map(post -> {
      List<String> preferTags = tagRepository.findByPostId(post.getId())
          .stream()
          .filter(tag -> tag.getTagType() == TagType.PREFER)
          .map(Tags::getTitle)
          .toList();
      return JoinPostSummaryDto.fromEntity(post, preferTags);
    }).toList();
  }

  @Transactional
  public void saveRecentView(Long userId, Long postId, PostType postType) { // 이전에 본 기록이 있는지 확인
    RecentViews existing = recentViewsRepository.findByUserIdAndPostIdAndPostType(userId, postId,
        postType);
    if (existing != null) { // 이전에 본 글이면 최근 본 시각만 업데이트
      existing.setViewedAt(LocalDateTime.now());
      recentViewsRepository.save(existing);
    } else { // 처음 본 글이면 새로 저장
      RecentViews recentViews = RecentViews.builder()
          .userId(userId)
          .postId(postId)
          .postType(postType)
          .viewedAt(LocalDateTime.now())
          .build();
      recentViewsRepository.save(recentViews);
    }
  }

  // 최근 본 동행글 목록 조회
  public List<JoinPostSummaryDto> getRecentViewPost(Long userId) {
    List<RecentViews> recentViews = recentViewsRepository.findTop30ByUserIdAndPostTypeOrderByViewedAtDesc(
        userId, PostType.JOIN_POST);

    return recentViews.stream().map(view -> {
      JoinPost post = joinPostRepository.findById(view.getId())
          .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수가 없습니다."));
      List<String> preferTags = tagRepository.findByPostId(post.getId())
          .stream()
          .filter(tag -> tag.getTagType() == TagType.PREFER)
          .map(Tags::getTitle)
          .toList();

      return JoinPostSummaryDto.fromEntity(post, preferTags);
    }).toList();
  }
}
