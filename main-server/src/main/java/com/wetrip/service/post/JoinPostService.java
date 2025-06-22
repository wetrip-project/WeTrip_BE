package com.wetrip.service.post;

import com.wetrip.dto.request.PostCreateRequestDto;
import com.wetrip.post.entity.Course;
import com.wetrip.post.entity.JoinPost;
import com.wetrip.post.entity.Tags;
import com.wetrip.post.enums.TagType;
import com.wetrip.post.repository.CourseRepository;
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

  @Transactional
  public void createPost(final User user, final PostCreateRequestDto request) {
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
            .title(title).build());
    var preferTas = request.preferTags().stream()
        .map(title -> Tags.builder()
            .tagType(TagType.PREFER)
            .title(title).build());

    var allTags = Stream.concat(travelTags, preferTas)
        .toList();

    tagRepository.saveAll(allTags);
  }
}
