package com.wetrip.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

@Builder
public class RoomInfo {
  private List<Long> users;

  public boolean isJoined(Long userId) {
    return users.contains(userId);
  }

  public void addUser(Long userId) {
    if (CollectionUtils.isEmpty(users)) {
      this.users = new ArrayList<>();
    }
    users.add(userId);
  }

  public void removeUser(Long userId) {
    if (CollectionUtils.isEmpty(users)) {
      return;
    }
    users.remove(userId);
  }

  public List<Long> getUsers() {
    return Collections.unmodifiableList(users);
  }
}
