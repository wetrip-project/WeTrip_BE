package com.wetrip.dto;

import lombok.Builder;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public record SessionInfo(
    Long userId,
    String roomId,
    Boolean isAuthenticated
) {

}
