package com.wetrip.dto;


import com.wetrip.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class OAuth2UserResponseDto {
    private String socialId;        // 소셜 플랫폼 고유 ID
    private String email;           // 이메일
    private String name;            // 닉네임
    private User.LoginType loginType; // 로그인 타입
    private Integer age;               // 나이
    private User.Gender gender;     // 성별
    private String contact;         // 연락처
    private String profileImage;    // 프로필 이미지 URL
    private LocalDate birthDate;    // 생일

    // 동의 정보
    private boolean ageConsentProvided;
    private boolean birthdayConsentProvided;
    private boolean contactConsentProvided;
    private boolean emailConsentProvided;
    private boolean genderConsentProvided;

    // DTO를 User 엔티티로 변환
    public User toEntity() {
        return User.builder()
                .socialId(this.socialId)
                .email(this.email)
                .name(this.name)
                .loginType(this.loginType)
                .age(this.age != null ? this.age : 0)
                .gender(this.gender != null ? this.gender : User.Gender.none)
                .contact(this.contact)
                .profileImage(this.profileImage)
                .birthDate(this.birthDate)
                .mbti(User.MBTI.none) // 기본값
                .build();
    }
}
