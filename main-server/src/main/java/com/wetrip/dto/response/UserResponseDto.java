package com.wetrip.dto.response;


import com.wetrip.entity.User;
import lombok.Getter;

@Getter
// 사용자 정보 응답용 DTO
public class UserResponseDto {
    private final String name;
    private final String email;
    private final String gender;
    private final String profileImage;
    private final String concat; // 연락처
    private final Integer age;
    private final String loginType;

    public UserResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.gender = user.getGender().toString();
        this.profileImage = user.getProfileImage();
        this.concat = user.getContact();
        this.age = user.getAge();
        this.loginType = user.getLoginType().toString();
    }
}
