package com.wetrip.dto;

import lombok.Builder;
import lombok.experimental.Accessors;

@Builder
public record SessionInfo(
    Long userId,
    String sessionId
) {

}
