package com.wetrip.dto.social;


import com.wetrip.dto.OAuth2UserResponseDto;
import com.wetrip.user.entity.User;
import com.wetrip.user.enums.LoginType;
import com.wetrip.utils.SocialResponseUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class NaverUserResponseDto {

  private String socialId;
  private String email;
  private String name;
  private String profileImage;
  private String age;
  private String gender;
  private String contact;
  private String birthday;
  private String birthyear;

  public static NaverUserResponseDto from(Map<String, Object> response) {
    return NaverUserResponseDto.builder()
        .socialId(SocialResponseUtil.safeStringFromMap(response, "id"))
        .email(SocialResponseUtil.safeStringFromMap(response, "email"))
        .name(SocialResponseUtil.safeStringOrDefault(response.get("name"), "네이버사용자"))
        .profileImage(SocialResponseUtil.safeStringFromMap(response, "profile_image"))
        .age(SocialResponseUtil.safeStringFromMap(response, "age"))
        .gender(SocialResponseUtil.safeStringFromMap(response, "gender"))
        .contact(SocialResponseUtil.safeStringFromMap(response, "mobile"))
        .birthday(SocialResponseUtil.safeStringFromMap(response, "birthday"))
        .birthyear(SocialResponseUtil.safeStringFromMap(response, "birthyear"))
        .build();
  }

  public OAuth2UserResponseDto toOAuth2UserInfo() {
    return OAuth2UserResponseDto.builder()
        .socialId(this.socialId)
        .email(this.email)
        .name(this.name)
        .loginType(LoginType.NAVER)
        .age(SocialResponseUtil.parseAge(this.age))
        .gender(SocialResponseUtil.parseGender(this.gender))
        .contact(this.contact)
        .profileImage(this.profileImage)
        .birthDate(SocialResponseUtil.parseBirthday(this.birthday, this.birthyear))
        .ageConsentProvided(this.age != null)
        .birthdayConsentProvided(this.birthday != null)
        .contactConsentProvided(this.contact != null)
        .emailConsentProvided(this.email != null)
        .genderConsentProvided(this.gender != null)
        .build();
  }
}
