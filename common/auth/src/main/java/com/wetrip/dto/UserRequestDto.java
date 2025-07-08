package com.wetrip.dto;

import com.wetrip.user.entity.User;
import java.util.List;
import lombok.Getter;

public class UserRequestDto {

    @Getter
    public static class NicknameRequest{
        private String name;
    }

    @Getter
    public static class GenderAgeRequest{
        private User.Gender gender;
        private Integer age;
    }

    @Getter
    public static class TripTypeRequest{
        private List<Long> tripTypeId;  //tripTypeName으로 해야 하는지
    }
}
