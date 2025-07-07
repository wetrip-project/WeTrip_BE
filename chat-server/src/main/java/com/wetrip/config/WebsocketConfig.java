package com.wetrip.config;

import com.wetrip.config.interceptor.AuthenticationInterceptor;
import com.wetrip.handler.ChatApplicationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketConfigurer {

  private final ChatApplicationHandler chatApplicationHandler;
  private final AuthenticationInterceptor authenticationInterceptor;


  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(chatApplicationHandler, "/chat")
        .addInterceptors(authenticationInterceptor)
        .setAllowedOrigins("*");
  }
}
