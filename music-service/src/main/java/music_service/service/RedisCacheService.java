package music_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Save a value to Redis cache
     *
     * @param key the key
     * @param value the value
     * @param ttl time-to-live in seconds
     */
    public void saveToCache(String key, Object value, long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    /**
     * Retrieve a value from Redis cache
     *
     * @param key the key
     * @return the value or null if not found
     */
    public Object getFromCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Delete a value from Redis cache
     *
     * @param key the key
     */
    public void deleteFromCache(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Check if a key exists in Redis cache
     *
     * @param key the key
     * @return true if the key exists, false otherwise
     */
    public boolean existsInCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Update a value in Redis cache
     *
     * @param key the key
     * @param value the new value
     * @param ttl time-to-live in seconds
     */
    public void updateInCache(String key, Object value, long ttl) {
        if (existsInCache(key)) {
            saveToCache(key, value, ttl);
        } else {
            throw new IllegalStateException("Cannot update. Key not found in cache: " + key);
        }
    }

    public Map<String, Object> getAllCache() {
        Set<String> keys = redisTemplate.keys("*"); // Fetch all keys in Redis
        if (keys == null || keys.isEmpty()) {
            return new HashMap<>(); // Return an empty map if no keys are found
        }

        Map<String, Object> cacheContents = new HashMap<>();
        for (String key : keys) {
            Object value = redisTemplate.opsForValue().get(key); // Get value for each key
            cacheContents.put(key, value);
        }
        return cacheContents;
    }
}
