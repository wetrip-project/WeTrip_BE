package com.wetrip.dto.social;

import com.wetrip.user.enums.LoginType;
import java.time.LocalDate;
import java.util.Map;
import com.wetrip.dto.OAuth2UserResponseDto;
import com.wetrip.user.entity.User;
import com.wetrip.utils.SocialResponseUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class GoogleUserResponseDto {

    private String socialId;
    private String email;
    private String name;
    private String profileImage;
    private String gender;
    private String birthday;
    private String contact;

    public static GoogleUserResponseDto from(Map<String, Object> attributes,
        Map<String, Object> additionalInfo) {
        return GoogleUserResponseDto.builder()
            .socialId(SocialResponseUtil.safeString(attributes.get("sub")))
            .email(SocialResponseUtil.safeString(attributes.get("email")))
            .name(SocialResponseUtil.safeStringOrDefault(attributes.get("name"), "구글사용자"))
            .profileImage(SocialResponseUtil.safeString(attributes.get("picture")))
            .gender(SocialResponseUtil.safeString(
                additionalInfo != null ? additionalInfo.get("gender") : null))
            .birthday(SocialResponseUtil.safeString(
                additionalInfo != null ? additionalInfo.get("birthday") : null))
            .contact(SocialResponseUtil.safeString(
                additionalInfo != null ? additionalInfo.get("phoneNumber") : null))
            .build();
    }

    public OAuth2UserResponseDto toOAuth2UserInfo() {
        LocalDate birthDate = SocialResponseUtil.parseBirthday(this.birthday);
        int age = birthDate != null ? LocalDate.now().getYear() - birthDate.getYear() : 0;

        return OAuth2UserResponseDto.builder()
            .socialId(this.socialId)
            .email(this.email)
            .name(this.name)
            .loginType(LoginType.GOOGLE)
            .age(age)
            .gender(SocialResponseUtil.parseGender(this.gender))
            .contact(this.contact)
            .profileImage(this.profileImage)
            .birthDate(birthDate)
            .ageConsentProvided(age > 0)
            .birthdayConsentProvided(birthDate != null)
            .contactConsentProvided(this.contact != null)
            .emailConsentProvided(this.email != null)
            .genderConsentProvided(this.gender != null)
            .build();
    }
}