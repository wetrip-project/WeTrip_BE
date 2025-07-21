package com.wetrip.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

@Builder
public class RoomInfo {
  private Set<Long> users;

  public boolean isJoined(Long userId) {
    return users.contains(userId);
  }

  public void addUser(Long userId) {
    if (CollectionUtils.isEmpty(users)) {
      this.users = new HashSet<>();
    }
    users.add(userId);
  }

  public void removeUser(Long userId) {
    if (CollectionUtils.isEmpty(users)) {
      return;
    }
    users.remove(userId);
  }

  public Set<Long> getUsers() {
    return Collections.unmodifiableSet(users);
  }
}
