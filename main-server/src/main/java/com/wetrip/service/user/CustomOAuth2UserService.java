package com.wetrip.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final KakaoOAuth2UserService kakaoService;
    private final GoogleOAuth2UserService googleService;
    private final NaverOAuth2UserService naverService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "kakao", "google" 등

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        return switch (registrationId) {
            case "kakao" -> kakaoService.process(userRequest, oAuth2User);
            case "google" -> googleService.process(userRequest, oAuth2User);
            case "naver" -> naverService.process(userRequest, oAuth2User);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        };
    }
}
