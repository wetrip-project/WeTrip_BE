package com.wetrip.config;

import com.wetrip.config.annotations.ChatMessageMapping;
import com.wetrip.dto.request.ChatMessage;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.socket.WebSocketSession;

@Component
@Slf4j
public class MessageMappingHandler implements ApplicationContextAware {

  private ApplicationContext applicationContext;
  private final Map<String, HandlerMethod> handlers = new HashMap<>();

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @PostConstruct
  public void scanMapping() {
    var beans = applicationContext.getBeansOfType(Object.class);

    beans.values().forEach(bean -> {
      var methods = bean.getClass().getDeclaredMethods();

      for (var method : methods) {
        var annotation = method.getAnnotation(ChatMessageMapping.class);
        if (annotation != null) {
          var path = annotation.path();
          log.debug("Found @ChatMessageMapping for path: {}", path);
          handlers.put(path, new HandlerMethod(bean, method));
        }
      }
    });
  }

  public Object routeMessage(String path, ChatMessage payload, WebSocketSession session)
      throws Exception {
    var handler = handlers.get(path);
    if (handler == null) {
      throw new IllegalArgumentException("No handler found for path: " + path);
    }

    var arguments = mappingArguments(payload, session, handler);

    return handler.getMethod().invoke(handler.getBean(), arguments);
  }

  private Object[] mappingArguments(ChatMessage payload, WebSocketSession session,
      HandlerMethod handler) {
    var method = handler.getMethod();
    var parameters = method.getParameters();
    var arguments = new Object[parameters.length];

    for (int i = 0; i < parameters.length; i++) {
      var parameter = parameters[i];

      if (parameter.getType().equals(ChatMessage.class)) {
        arguments[i] = payload;
      } else if (parameter.getType().equals(WebSocketSession.class)) {
        arguments[i] = session;
      } else {
        arguments[i] = null;
      }
    }

    return arguments;
  }

}
