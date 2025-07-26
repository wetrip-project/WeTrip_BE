package com.wetrip.service.post;

import com.wetrip.dto.request.PostCreateRequestDto;
import com.wetrip.post.entity.Course;
import com.wetrip.post.entity.JoinBookmark;
import com.wetrip.post.entity.JoinPost;
import com.wetrip.post.entity.Tags;
import com.wetrip.post.enums.PostType;
import com.wetrip.post.enums.TagType;
import com.wetrip.post.repository.CourseRepository;
import com.wetrip.post.repository.JoinBookmarkRepository;
import com.wetrip.post.repository.JoinPostRepository;
import com.wetrip.post.entity.Thumbnail;
import com.wetrip.post.repository.TagRepository;
import com.wetrip.post.repository.ThumbnailRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.wetrip.user.entity.User;

@Service
@RequiredArgsConstructor
public class JoinPostService {

  private final JoinPostRepository joinPostRepository;
  private final ThumbnailRepository thumbnailRepository;
  private final CourseRepository courseRepository;
  private final TagRepository tagRepository;
  private final JoinBookmarkRepository joinBookmarkRepository;

  @Transactional
  public Long createPost(final User user, final PostCreateRequestDto request) {
    JoinPost joinPost = request.toEntity(user, request);
    final var newPost = joinPostRepository.save(joinPost);

    var thumbnails = request.thumbnails().stream()
        .map(url -> Thumbnail.builder()
            .url(url)
            .postId(newPost.getId()).build())
        .toList();
    thumbnailRepository.saveAll(thumbnails);

    var course = IntStream.range(0, request.course().size())
        .mapToObj(index -> Course.builder()
            .order(index + 1)
            .title(request.course().get(index))
            .postId(newPost.getId()).build())
        .collect(Collectors.toList());
    courseRepository.saveAll(course);

    var travelTags = request.travelTags().stream()
        .map(title -> Tags.builder()
            .tagType(TagType.TRAVEL)
            .title(title)
            .postId(newPost.getId()).build());
    var preferTas = request.preferTags().stream()
        .map(title -> Tags.builder()
            .tagType(TagType.PREFER)
            .title(title)
            .postId(newPost.getId()).build());

    var allTags = Stream.concat(travelTags, preferTas)
        .toList();

    tagRepository.saveAll(allTags);

    return newPost.getId();
  }

  // 스크랩 추가 및 삭제
  @Transactional
  public boolean toggleScrap(Long userId, Long postId, PostType postType) {
    boolean exists = joinBookmarkRepository.existsByUserIdAndPostIdAndPostType(userId, postId,
        postType); // 스크랩 여부 확인

    JoinPost joinPost = joinPostRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다"));

    if (exists) { // 스크랩 해제
      joinBookmarkRepository.deleteByUserIdAndPostIdAndPostType(userId, postId, postType);
      joinPost.setScarpCount(joinPost.getScarpCount() - 1); // 스크랩 수 감소
    } else { // 스크랩 추가
      JoinBookmark bookmark = JoinBookmark.builder()
          .userId(userId)
          .postId(postId)
          .postType(postType)
          .state(Boolean.TRUE)
          .build();
      joinBookmarkRepository.save(bookmark);
      joinPost.setScarpCount(joinPost.getScarpCount() + 1);
    }

    joinPostRepository.save(joinPost);
    return !exists; // 현재 스크랩 상태 반환 - true면 스크랩됨
  }
}
