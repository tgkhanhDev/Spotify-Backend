package music_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import music_service.service.CaffeineCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/caffeine-cache")
@Tag(name = "For Testing Cache L1", description = "For Caching Caffeine (Last for 15s)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CaffeineCacheController {

    CacheManager cacheManager;
    CaffeineCacheService cacheService;

    @Autowired
    public CaffeineCacheController(CacheManager cacheManager, CaffeineCacheService cacheService) {
        this.cacheManager = cacheManager;
        this.cacheService = cacheService;
    }

//    @GetMapping("/get-all-keys")
//    public void getAllCacheKeys() throws JsonProcessingException {
//        cacheManager.getCacheNames().forEach(cacheName -> {
//            System.out.println("Cache Name: " + cacheName);
//            // Get the cache by name
//            Cache cache = cacheManager.getCache(cacheName);
//            System.out.println("test: "+ cache.get(cacheName));
//
//            if (cache != null) {
//                // Get the native Caffeine cache
//                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
//                        (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
//
//                // Iterate through entries in the cache
//                nativeCache.asMap().forEach((key, value) -> {
//                    System.out.println("    Key: " + key + ", Value: " + value);
//                });
//
//            } else {
//                System.out.println("    Cache is null or empty.");
//            }
//        });
//
//    }

//    @GetMapping("/{cache-name}/{key}")
//    public Map<String, Object> getCacheValueByCacheNameAndKey(@PathVariable String cacheName ,@PathVariable String key) throws JsonProcessingException {
//        return null;
//    }
//
//    @PostMapping("/setCache/{key}/{value}")
//    public Cache setCacheValueByKey(@PathVariable String key, @PathVariable String value) {
//        return (Cache) cacheManager.getCache(key).putIfAbsent(key, value);
//    }

    @GetMapping("/all")
    public Object getAllFromCache() {
        return cacheService.findAll();
    }

    @PostMapping("/save")
    public String saveToCache(@RequestParam String cacheName, @RequestParam String key, @RequestBody Object value) {
        cacheService.save(cacheName, key, value);
        return "Value saved to cache.";
    }

    @GetMapping("/get")
    public Object getFromCache(@RequestParam String cacheName, @RequestParam String key) {
        return cacheService.find(cacheName, key, Object.class);
    }

    @DeleteMapping("/delete")
    public String deleteFromCache(@RequestParam String cacheName, @RequestParam String key) {
        cacheService.delete(cacheName, key);
        return "Key removed from cache.";
    }

    @DeleteMapping("/clear")
    public String clearCache(@RequestParam String cacheName) {
        cacheService.clear(cacheName);
        return "Cache cleared.";
    }

}
