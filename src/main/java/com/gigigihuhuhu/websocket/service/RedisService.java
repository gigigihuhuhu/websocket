package com.gigigihuhuhu.websocket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}
