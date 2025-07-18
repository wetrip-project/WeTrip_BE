package com.wetrip.dto;


import com.wetrip.user.entity.User;
import com.wetrip.user.enums.Gender;
import com.wetrip.user.enums.LoginType;
import java.util.List;
import lombok.Getter;

@Getter
// 사용자 정보 응답용 DTO
public class UserResponseDto {

  private final String name;
  private final String email;
  private final Gender gender;
  private final String profileImage;
  private final String contact; // 연락처
  private final Integer age;
  private final LoginType loginType;
  private final List<Long> tripTypeId;

  public UserResponseDto(User user, List<Long> tripTypeId) {
    this.name = user.getName();
    this.email = user.getEmail();
    this.gender = user.getGender();
    this.profileImage = user.getProfileImage();
    this.contact = user.getContact();
    this.age = user.getAge();
    this.loginType = user.getLoginType();
    this.tripTypeId = tripTypeId;
  }
}
