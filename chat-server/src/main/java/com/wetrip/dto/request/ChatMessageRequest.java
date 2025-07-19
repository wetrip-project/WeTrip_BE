package com.wetrip.dto.request;

public record ChatMessageRequest(
    String type,
    String roomId,
    String userId,
    String message
) {
}
