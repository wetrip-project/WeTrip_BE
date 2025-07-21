package com.wetrip.dto.request;

public record ChatMessageRequest(
    String sendType,
    String roomId,
    String userId,
    String message
) {
}
