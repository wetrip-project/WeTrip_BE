package com.wetrip.dto.request;

public record ChatMessage(
    String type,
    String path,
    String roomId,
    String userId,
    String message
) {
}
