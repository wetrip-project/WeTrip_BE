package dto.social;


import dto.OAuth2UserResponseDto;
import user.entity.User;
import utils.SocialResponseUtil;
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
                .socialId(SocialResponseUtil.safeString(response.get("id")))
                .email(SocialResponseUtil.safeString(response.get("email")))
                .name(SocialResponseUtil.safeStringOrDefault(response.get("name"), "네이버사용자"))
                .profileImage(SocialResponseUtil.safeString(response.get("profile_image")))
                .age(SocialResponseUtil.safeString(response.get("age")))
                .gender(SocialResponseUtil.safeString(response.get("gender")))
                .contact(SocialResponseUtil.safeString(response.get("mobile")))
                .birthday(SocialResponseUtil.safeString(response.get("birthday")))
                .birthyear(SocialResponseUtil.safeString(response.get("birthyear")))
                .build();
    }

    public OAuth2UserResponseDto toOAuth2UserInfo() {
        return OAuth2UserResponseDto.builder()
                .socialId(this.socialId)
                .email(this.email)
                .name(this.name)
                .loginType(User.LoginType.NAVER)
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
