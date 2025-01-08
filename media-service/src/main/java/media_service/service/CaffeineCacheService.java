package media_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaffeineCacheService {

    CacheManager caffeineCacheManager;

    @Autowired
    public CaffeineCacheService(CacheManager caffeineCacheManager) {
        this.caffeineCacheManager = caffeineCacheManager;
    }

    public void save(String cacheName, String key, Object value) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        } else {
            throw new IllegalArgumentException("Cache with name '" + cacheName + "' does not exist.");
        }
    }

    public <T> T find(String cacheName, String key, Class<T> type) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            return cache.get(key, type);
        }
        throw new IllegalArgumentException("Cache with name '" + cacheName + "' does not exist.");
    }

    public Map<String, Object> findAll() {
        //result
        Map<String, Object> result = new HashMap<>();

        // Iterate over the cache names
        caffeineCacheManager.getCacheNames().forEach((cacheName) -> {
//            System.out.println("cacheName: " + cacheName);
            Cache cache = caffeineCacheManager.getCache(cacheName);
//            System.out.println("cache: " + cache);
            if(cache != null){
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
                // Iterate over the keys
                nativeCache.asMap().forEach((key,value) -> {
                    result.put(cacheName + ":: " + key, value);
                });
            }
        });
        return result;

    }

    public void delete(String cacheName, String key) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        } else {
            throw new IllegalArgumentException("Cache with name '" + cacheName + "' does not exist.");
        }
    }

    public void clear(String cacheName) {
        Cache cache = caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            throw new IllegalArgumentException("Cache with name '" + cacheName + "' does not exist.");
        }
    }
}
