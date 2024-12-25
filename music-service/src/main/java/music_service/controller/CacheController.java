package music_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/cache")
@Tag(name = "For Testing Cache", description = "For Caching")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CacheController {

    CacheManager cacheManager;

    @Autowired
    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/get-all-keys")
    public void getAllCacheKeys() throws JsonProcessingException {
        cacheManager.getCacheNames().forEach(cacheName -> {
            System.out.println("Cache Name: " + cacheName);
            // Get the cache by name
            Cache cache = cacheManager.getCache(cacheName);
            System.out.println("test: "+ cache.get(cacheName));

            if (cache != null) {
                // Get the native Caffeine cache
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                        (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();

                // Iterate through entries in the cache
                nativeCache.asMap().forEach((key, value) -> {
                    System.out.println("    Key: " + key + ", Value: " + value);
                });

            } else {
                System.out.println("    Cache is null or empty.");
            }
        });

    }

    @GetMapping("/{cache-name}/{key}")
    public Map<String, Object> getCacheValueByCacheNameAndKey(@PathVariable String cacheName ,@PathVariable String key) throws JsonProcessingException {
        return null;
    }

    @PostMapping("/setCache/{key}/{value}")
    public Cache setCacheValueByKey(@PathVariable String key, @PathVariable String value) {
        return (Cache) cacheManager.getCache(key).putIfAbsent(key, value);
    }

}
