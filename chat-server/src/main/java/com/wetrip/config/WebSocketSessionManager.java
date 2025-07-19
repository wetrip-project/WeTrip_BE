package com.wetrip.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class WebSocketSessionManager {

  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  public void addSession(WebSocketSession session) {
    sessions.put(session.getId(), session);
  }

  public void removeSession(String sessionId) {
    var session = sessions.remove(sessionId);
  }

  public WebSocketSession get(String sessionId) {
    return sessions.get(sessionId);
  }
}
