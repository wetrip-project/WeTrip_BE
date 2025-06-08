package com.wetrip.service.user;

import com.wetrip.dto.response.OAuth2UserResponseDto;
import com.wetrip.dto.response.social.GoogleUserResponseDto;
import com.wetrip.entity.Tokens;
import com.wetrip.entity.User;
import com.wetrip.entity.UserAgreement;
import com.wetrip.repository.TokensRepository;
import com.wetrip.repository.UserRepository;
import com.wetrip.util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.wetrip.entity.User.LoginType.GOOGLE;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2UserService implements OAuth2UserServiceInterface {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokensRepository tokensRepository;
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
        User savedUser = existing.orElseGet(() -> {
            User newUser = dto.toEntity();

            UserAgreement agreement = UserAgreement.builder()
                    .user(newUser)
                    .ageConsentProvided(dto.isAgeConsentProvided())
                    .birthdayConsentProvided(dto.isBirthdayConsentProvided())
                    .contactConsentProvided(dto.isContactConsentProvided())
                    .emailConsentProvided(dto.isEmailConsentProvided())
                    .genderConsentProvided(dto.isGenderConsentProvided())
                    .build();

            newUser.setUserAgreement(agreement);
            return userRepository.save(newUser);
        });

        // 토큰 발급 및 저장
        Tokens tokens = Tokens.builder()
                .user(savedUser)
                .accessToken(jwtTokenProvider.createAccessToken(savedUser.getId().toString()))
                .refreshToken(jwtTokenProvider.createRefreshToken(savedUser.getId().toString()))
                .socialAccessToken(request.getAccessToken().getTokenValue())
                .createdAt(LocalDateTime.now())
                .build();

        tokensRepository.save(tokens);

        Map<String, Object> attributesWithUserId = new HashMap<>(attributes);
        attributesWithUserId.put("userId", savedUser.getId());

        // Oath2user 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributesWithUserId,
                "sub"
        );
    }
}