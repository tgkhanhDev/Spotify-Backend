package media_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import media_service.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/redis-cache")
@Tag(name = "For Testing Cache L2", description = "For Caching Redis (Last for 30s)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisCacheController {

    RedisCacheService redisCacheService;

    @Autowired
    public RedisCacheController(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    @PostMapping("/save")
    public String saveToCache(@RequestParam String key, @RequestParam String value, @RequestParam long ttl) {
        redisCacheService.saveToCache(key, value, ttl);
        return "Saved to cache.";
    }

    @GetMapping("/get")
    public Object getFromCache(@RequestParam String key) {
        return redisCacheService.getFromCache(key);
    }

    @DeleteMapping("/delete")
    public String deleteFromCache(@RequestParam String key) {
        redisCacheService.deleteFromCache(key);
        return "Deleted from cache.";
    }

    @PutMapping("/update")
    public String updateInCache(@RequestParam String key, @RequestParam String value, @RequestParam long ttl) {
        redisCacheService.updateInCache(key, value, ttl);
        return "Updated in cache.";
    }

    @GetMapping("/exists")
    public boolean existsInCache(@RequestParam String key) {
        return redisCacheService.existsInCache(key);
    }
    @GetMapping("/all")
    public Map<String, Object> getAllCache() {
        return redisCacheService.getAllCache();
    }
}
