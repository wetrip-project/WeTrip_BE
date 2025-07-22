package com.wetrip.utils;

import org.springframework.stereotype.Component;

@Component
public final class RedisKeyUtils {

  private final String USER_SESSION_KEY = "user:{%}";
  private final String ROOM_USER_KEY = "room:{%}";

//  public static String getUserKey()

  private RedisKeyUtils() {}
}
