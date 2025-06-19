package com.wetrip.dto.request;

import lombok.Builder;
import post.entity.JoinPost;
import user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostCreateRequestDto(
        String title,
        String country,
        String city,
        LocalDate tripStartDate,
        LocalDate tripEndDate,
        List<String> thumbnail,
        Integer groupNumber,
        LocalDate recruitEndDate,
        String introduction,
        List<String> course,
        List<String> tripTags,
        List<String> preferTags
) {

    public JoinPost toEntity(User user, PostCreateRequestDto request) {
        return JoinPost.builder()
                .title(title).build();
//                .tar
    }
}
