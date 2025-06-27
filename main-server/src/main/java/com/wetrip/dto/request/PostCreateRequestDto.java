package com.wetrip.dto.request;

import com.wetrip.post.entity.JoinPost.RecruitmentStatus;
import lombok.Builder;
import com.wetrip.post.entity.JoinPost;
import com.wetrip.user.entity.User;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PostCreateRequestDto(
    String title,
    String country,
    String city,
    LocalDate travelStartDate,
    LocalDate travelEndDate,
    List<String> thumbnails,
    Integer recruitmentCount,
    LocalDate recruitEndDate,
    String introduction,
    List<String> course,
    List<String> travelTags,
    List<String> preferTags
) {

  public JoinPost toEntity(User user, PostCreateRequestDto request) {
    return JoinPost.builder()
        .title(title)
        .travelCountry(country)
        .travelCity(city)
        .travelStartDate(travelStartDate)
        .travelEndDate(travelEndDate)
        .recruitmentCount(recruitmentCount)
        .recruitmentEndDate(recruitEndDate)
        .recruitmentStatus(RecruitmentStatus.recruiting)
        .introduction(introduction)
        .author(user)
        .viewCount(0L)
        .build();
  }
}
