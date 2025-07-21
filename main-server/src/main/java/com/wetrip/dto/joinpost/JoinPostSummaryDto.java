package com.wetrip.dto.joinpost;

import com.wetrip.post.entity.JoinPost;
import com.wetrip.post.entity.Tags;
import com.wetrip.post.enums.RecruitmentStatus;
import com.wetrip.post.enums.Tag;
import com.wetrip.post.enums.TagType;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinPostSummaryDto { // 사용자가 작성한 글 리스트에서 보여줄 내용

  private Long postId;
  private String postTitle;
  private String postIntroduction;
  private RecruitmentStatus recruitmentStatus; // 모집 상태(모집 중, 완료)
  private LocalDate travelStartDate; // 여행 시작 날짜
  private LocalDate travelEndDate; // 여행 끝 날짜
  private List<String> preferTags; // 나이,연령대 등
  private String thumbnailUrl; // 썸네일

  public static JoinPostSummaryDto fromEntity(JoinPost post, List<String> preferTags) {

    return JoinPostSummaryDto.builder()
        .postId(post.getId())
        .postTitle(post.getTitle())
        .postIntroduction(post.getIntroduction())
        .recruitmentStatus(post.getRecruitmentStatus())
        .travelStartDate(post.getTravelStartDate())
        .travelEndDate(post.getTravelEndDate())
        .preferTags(preferTags) // 선호 태그(성별, 연령대)만 담음
        .thumbnailUrl(post.getThumbnails().isEmpty() ? null : post.getThumbnails().get(0).getUrl())
        .build();
  }
}
