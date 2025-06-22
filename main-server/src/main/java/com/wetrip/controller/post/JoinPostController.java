package com.wetrip.controller.post;

import com.wetrip.dto.request.PostCreateRequestDto;
import com.wetrip.service.post.JoinPostService;
import com.wetrip.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public void createPost(
      Authentication authentication,
      @RequestBody final PostCreateRequestDto postCreateRequestDto) {
    String userId = (String) authentication.getPrincipal();
    User user = userService.findByUser(Long.parseLong(userId));

    joinPostService.createPost(user, postCreateRequestDto);
  }
}
