package com.wetrip.service.Redis;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisOnboardingService {

    private final RedisTemplate<String, Object> redisTemplate;

    private String key(Long userId) {
        return "onboarding:" + userId;
    }

    // 한 항목 저장
    public void saveStep(Long userId, String field, Object value) {
        redisTemplate.opsForHash().put(key(userId), field, value);
    }

    // 모든 항목 조회
    public Map<Object, Object> getAll(Long userId) {
        return redisTemplate.opsForHash().entries(key(userId));
    }

    // 전체 삭제
    public void delete(Long userId) {
        redisTemplate.delete(key(userId));
    }
}
