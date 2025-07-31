package com.wetrip.service;

import com.wetrip.dto.OAuth2UserResponseDto;
import com.wetrip.dto.TokenInfoDto;
import com.wetrip.dto.social.KakaoUserResponseDto;
import com.wetrip.user.entity.User;
import com.wetrip.user.entity.UserAgreement;
import com.wetrip.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wetrip.utils.JwtTokenProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService implements OAuth2UserServiceInterface {

  private final JwtTokenProvider jwtTokenProvider;
  private final SessionTokenService sessionTokenService;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public OAuth2User process(OAuth2UserRequest request, OAuth2User user) {

    // 사용자 정보 추출
    Map<String, Object> attributes = user.getAttributes();

    // DTO로 변환
    KakaoUserResponseDto kakaoDto = KakaoUserResponseDto.from(attributes);
    OAuth2UserResponseDto dto = kakaoDto.toOAuth2UserInfo();

    // 기존 사용자 조회
    Optional<User> existing = userRepository.findBySocialId(dto.getSocialId());

    // 사용자 없으면 사용자 생성
    User savedUser = existing.orElseGet(() -> createUserAgreement(dto));

    // 토큰 발급
    String accessToken = jwtTokenProvider.createAccessToken(savedUser.getId().toString());
    String refreshToken = jwtTokenProvider.createRefreshToken(savedUser.getId().toString());

    // TokenInfo 생성
    TokenInfoDto tokenInfo = TokenInfoDto.createTokenInfo(
        savedUser.getId(),
        accessToken,
        refreshToken,
        request.getAccessToken().getTokenValue(),
        null, // 카카오 리프레시 토큰
        "kakao"
    );

    // redis 세션에 토큰 저장
    sessionTokenService.saveRefreshToken(savedUser.getId(), tokenInfo.getRefreshToken());

    Map<String, Object> customAttributes = new HashMap<>(attributes);
    customAttributes.put("userId", savedUser.getId());

    // Oath2user 반환
    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), // 해당 사용자의 권한
        customAttributes, // 사용자의 상세 정보 - Map 형식
        "id" // customAttributes 에서 사용자 식별자로 사용할 키
    );
  }

  private User createUserAgreement(OAuth2UserResponseDto dto) { // 메서드로 분리
    User newUser = dto.toEntity();
    UserAgreement agreement = UserAgreement.from(
        newUser,
        dto.isAgeConsentProvided(),
        dto.isBirthdayConsentProvided(),
        dto.isContactConsentProvided(),
        dto.isEmailConsentProvided(),
        dto.isGenderConsentProvided()
    );
    newUser.setUserAgreement(agreement);
    return userRepository.save(newUser);
  }
}