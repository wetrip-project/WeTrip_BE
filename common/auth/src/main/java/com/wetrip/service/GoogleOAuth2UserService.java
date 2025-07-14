package com.wetrip.service;

import com.wetrip.dto.OAuth2UserResponseDto;
import com.wetrip.dto.TokenInfoDto;
import com.wetrip.dto.social.GoogleUserResponseDto;
import com.wetrip.user.entity.User;
import com.wetrip.user.entity.UserAgreement;
import com.wetrip.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.wetrip.utils.JwtTokenProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2UserService implements OAuth2UserServiceInterface {

  private final JwtTokenProvider jwtTokenProvider;
  private final SessionTokenService sessionTokenService;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public OAuth2User process(OAuth2UserRequest request, OAuth2User user) {

    // 사용자 정보 추출
    Map<String, Object> attributes = user.getAttributes();
    Map<String, Object> additionalInfo = new HashMap<>(); // 성별, 생일, 전화번호가 따로 온다면 여기에

    // DTO로 변환
    GoogleUserResponseDto googleDto = GoogleUserResponseDto.from(attributes, additionalInfo);
    OAuth2UserResponseDto dto = googleDto.toOAuth2UserInfo();

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
        null, // 구글은 보통 리프레쉬 토큰 제공 X
        "google"
    );

    // redis 세션에 토큰 저장
    sessionTokenService.saveTokenInfo(savedUser.getId(), tokenInfo);

    Map<String, Object> attributesWithUserId = new HashMap<>(attributes);
    attributesWithUserId.put("userId", savedUser.getId());

    // Oath2user 반환
    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
        attributesWithUserId,
        "sub"
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