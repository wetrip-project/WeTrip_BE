package com.wetrip.controller.post;

import com.wetrip.dto.request.PostCreateRequestDto;
import com.wetrip.post.enums.PostType;
import com.wetrip.service.post.JoinPostService;
import com.wetrip.service.user.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wetrip.user.entity.User;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class JoinPostController {

  private final JoinPostService joinPostService;
  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Long createPost(
      Authentication authentication,
      @RequestBody final PostCreateRequestDto postCreateRequestDto) {
    String userId = (String) authentication.getPrincipal();
    User user = userService.findByUser(Long.parseLong(userId));

    return joinPostService.createPost(user, postCreateRequestDto);
  }

  @PostMapping("/scrap")
  public ResponseEntity<Map<String, Object>> scrapPost(Authentication authentication,
      @RequestParam Long postId, @RequestParam PostType postType) {

    Long userId = Long.parseLong(authentication.getName());
    boolean isScrapped = joinPostService.toggleScrap(userId, postId, postType);

    return ResponseEntity.ok(Map.of(
        "message", isScrapped ? "스크랩 추가됨" : "스크랩 해제됨",
        "isScrapped", isScrapped
    ));
  }
}
