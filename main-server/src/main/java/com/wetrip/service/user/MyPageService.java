package com.wetrip.service.user;

import com.wetrip.community.repository.CommunityRepository;
import com.wetrip.dto.joinpost.JoinPostSummaryDto;
import com.wetrip.post.entity.JoinBookmark;
import com.wetrip.post.entity.JoinPost;
import com.wetrip.post.entity.Tags;
import com.wetrip.post.enums.PostType;
import com.wetrip.post.enums.RecruitmentStatus;
import com.wetrip.post.enums.TagType;
import com.wetrip.post.repository.JoinBookmarkRepository;
import com.wetrip.post.repository.JoinPostRepository;
import com.wetrip.post.repository.TagRepository;
import com.wetrip.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final JoinPostRepository joinPostRepository;
  private final TagRepository tagRepository;
  private final JoinBookmarkRepository joinBookmarkRepository;
  private final CommunityRepository communityRepository;

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

  public List<JoinPostSummaryDto> getScrapPost(Long userId) { // 동행글 북마크 목록 조회
    List<JoinBookmark> bookmarks = joinBookmarkRepository.findByUserIdAndPostTypeOrderByIdDesc(
        userId, PostType.JOIN_POST);
    return bookmarks.stream().map(JoinBookmark -> {
      JoinPost post = joinPostRepository.findById(JoinBookmark.getPostId())
          .orElseThrow(() -> new IllegalArgumentException("해당 동행글을 찾을 수 없습니다."));
      return JoinPostSummaryDto.fromEntity(post, List.of());
    }).toList();
  }

/*  public List<JoinPostSummaryDto> getScrapCommunity(Long userId) { // 커뮤니티 북마크 목록 조회 - 보류
    List<JoinBookmark> bookmarks = joinBookmarkRepository.findByUserIdAndPostTypeOrderByCreatedAtDesc(
        userId, PostType.COMMUNITY);
    return bookmarks.stream().map(JoinBookmark -> {
      Community community = communityRepository.findById(JoinBookmark.getPostId())
          .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티글을 찾을 수 없습니다."));
      return JoinPostSummaryDto.fromEntity(community);
    }).toList();
  }*/
}
