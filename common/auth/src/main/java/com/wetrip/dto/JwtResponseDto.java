package com.wetrip.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
// 로그인 응답용 DTO
public class JwtResponseDto {
    private String accessToken;
    private String refreshToken;
}

