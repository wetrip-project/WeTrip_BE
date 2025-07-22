package com.wetrip.controller.user;

import com.wetrip.dto.joinpost.JoinPostSummaryDto;
import com.wetrip.post.enums.RecruitmentStatus;
import com.wetrip.service.user.MyPageService;
import com.wetrip.user.entity.User;
import com.wetrip.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/mypage")
@RequiredArgsConstructor
public class MyPageController {

  private final MyPageService myPageService;
  private final UserRepository userRepository;

  @GetMapping("/posts")
  public List<JoinPostSummaryDto> getMyPosts(Authentication authentication, @RequestParam
  RecruitmentStatus status) {
    DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
    Long userId = oauthUser.getAttribute("userId");

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    return myPageService.getMyPostStatus(user, status);
  }
}
