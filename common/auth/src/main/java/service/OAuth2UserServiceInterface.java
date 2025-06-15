package service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserServiceInterface {
    OAuth2User process(OAuth2UserRequest request, OAuth2User user); // 사용자 요청 정보와 사용자 데이터를 받아 처리하는 공통 메서드
}
