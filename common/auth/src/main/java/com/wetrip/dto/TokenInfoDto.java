package com.wetrip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoDto {

  private Long userId;
  private String accessToken;
  private String refreshToken;
  private String socialAccessToken;
  private String socialRefreshToken;
  private String provider; // kakao, google, naver

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime lastAccessedAt;

  public static TokenInfoDto createTokenInfo(Long userId, String accessToken, String refreshToken,
      String socialAccessToken, String socialRefreshToken, String provider) {
    return TokenInfoDto.builder()
        .userId(userId)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .socialAccessToken(socialAccessToken)
        .socialRefreshToken(socialRefreshToken)
        .provider(provider).build();
  }

  public void updateLastAccessedAt() {
    this.lastAccessedAt = LocalDateTime.now();
  }
}
