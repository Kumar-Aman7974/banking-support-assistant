package com.banking.support.aiservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofHours(1);

    public Optional<String> get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.info("CACHE HIT: {}", key);
                return Optional.of(value.toString());
            }
            log.info("CACHE MISS: {}", key);
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Redis GET failed for key {} — falling back to source. Reason: {}", key, e.getMessage());
            return Optional.empty();  // Fail open — app keeps working
        }
    }

    public void put(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value, TTL);
            log.info("CACHE PUT: {} (TTL {}s)", key, TTL.getSeconds());
        } catch (Exception e) {
            log.warn("Redis PUT failed for key {}. Reason: {}", key, e.getMessage());

        }
    }
}
