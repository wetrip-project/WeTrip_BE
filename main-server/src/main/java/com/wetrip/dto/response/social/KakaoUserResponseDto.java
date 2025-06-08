package com.wetrip.dto.response.social;


import com.wetrip.entity.User;
import com.wetrip.util.SocialResponseUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import com.wetrip.dto.response.OAuth2UserResponseDto;

@Getter
@Setter
@Builder
public class KakaoUserResponseDto { // 카카오 로그인 - 이중 구조
    private String id;
    private String email;
    private String name;
    private String profileImageUrl;
    private String ageRange;
    private String gender;
    private String contact;
    private String birthday;
    private String birthyear;

    public static KakaoUserResponseDto from(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;

        return KakaoUserResponseDto.builder()
                .id(SocialResponseUtil.safeString(attributes.get("id")))
                .email(SocialResponseUtil.safeString(kakaoAccount != null ? kakaoAccount.get("email") : null))
                .name(SocialResponseUtil.safeStringOrDefault(profile != null ? profile.get("nickname") : null, "카카오사용자")) // 카카오는 이름 제공 X - 닉네임으로 받아옴
                .profileImageUrl(SocialResponseUtil.safeString(profile != null ? profile.get("profile_image_url") : null))
                .ageRange(SocialResponseUtil.safeString(kakaoAccount != null ? kakaoAccount.get("age_range") : null))
                .gender(SocialResponseUtil.safeString(kakaoAccount != null ? kakaoAccount.get("gender") : null))
                .contact(SocialResponseUtil.safeString(kakaoAccount != null ? kakaoAccount.get("phone_number") : null))
                .birthday(SocialResponseUtil.safeString(kakaoAccount != null ? kakaoAccount.get("birthday") : null))
                .birthyear(SocialResponseUtil.safeString(kakaoAccount != null ? kakaoAccount.get("birthyear") : null))
                .build();
    }

    public OAuth2UserResponseDto toOAuth2UserInfo() {
        return OAuth2UserResponseDto.builder()
                .socialId(this.id)
                .email(this.email)
                .name(this.name)
                .loginType(User.LoginType.KAKAO)
                .age(SocialResponseUtil.parseAgeFromRange(this.ageRange))
                .gender(SocialResponseUtil.parseGender(this.gender))
                .contact(this.contact)
                .profileImage(this.profileImageUrl)
                .birthDate(SocialResponseUtil.parseBirthday(this.birthday, this.birthyear))
                .ageConsentProvided(this.ageRange != null)
                .birthdayConsentProvided(this.birthday != null)
                .contactConsentProvided(this.contact != null)
                .emailConsentProvided(this.email != null)
                .genderConsentProvided(this.gender != null)
                .build();
    }
}