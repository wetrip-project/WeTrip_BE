package com.wetrip.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;

  public void setObject(String key, Object value) {
    try {
      String jsonValue = objectMapper.writeValueAsString(value);
      stringRedisTemplate.opsForValue().set(key, jsonValue);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("객체를 JSON으로 직렬화할 수 없습니다.", e);
    }
  }

  public <T> T getObject(String key, Class<T> type) {
    String jsonValue = stringRedisTemplate.opsForValue().get(key);
    if (jsonValue == null) {
      return null;
    }

    try {
      return objectMapper.readValue(jsonValue, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON을 " + type.getSimpleName() + " 타입으로 역직렬화할 수 없습니다.", e);
    }
  }

  public void unlink(String key) {
    stringRedisTemplate.unlink(key);
  }

}
