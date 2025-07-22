package com.wetrip.service.user;

import com.wetrip.dto.joinpost.JoinPostSummaryDto;
import com.wetrip.post.entity.JoinPost;
import com.wetrip.post.entity.Tags;
import com.wetrip.post.enums.RecruitmentStatus;
import com.wetrip.post.enums.TagType;
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
}
