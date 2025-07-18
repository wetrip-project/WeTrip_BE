package com.wetrip.dto;

import com.wetrip.user.enums.Gender;
import java.util.List;
import lombok.Getter;

public class UserRequestDto {

    @Getter
    public static class NicknameRequest{
        private String name;
    }

    @Getter
    public static class GenderAgeRequest{
        private Gender gender;
        private Integer age;
    }

    @Getter
    public static class TripTypeRequest{
        private List<Long> tripTypeId;
    }

    @Getter
    public static class ProfileUploadRequest{
        private String fileName;
    }
}
