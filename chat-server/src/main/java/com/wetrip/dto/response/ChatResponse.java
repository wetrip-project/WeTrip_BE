package com.wetrip.dto.response;

import com.wetrip.chat.entity.ChatMessage;
import lombok.Builder;

@Builder
public record ChatResponse(
    ResponseType type,
    int status,
    String message
) {

  public static ChatResponse success() {
    return ChatResponse.builder().status(200).build();
  }

  public static ChatResponse success(ResponseType type, String message) {
    return ChatResponse.builder()
        .type(type).status(200).message(message).build();
  }

  public static ChatResponse failed(String message) {
    return ChatResponse.builder()
        .type(ResponseType.RESPONSE).status(500).message(message).build();
  }
}
