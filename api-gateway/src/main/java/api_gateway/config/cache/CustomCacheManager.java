package api_gateway.config.cache;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomCacheManager implements CacheManager {

    CaffeineCacheManager caffeineCacheManager;
    RedisCacheManager redisCacheManager;

    @Autowired
    public CustomCacheManager(CaffeineCacheManager caffeineCacheManager, RedisCacheManager redisCacheManager) {
        this.caffeineCacheManager = caffeineCacheManager;
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        Cache caffeineCache = caffeineCacheManager.getCache(name);
        Cache redisCache = redisCacheManager.getCache(name);
        return new CustomCache(caffeineCache, redisCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        Set<String> names = new HashSet<>();
        names.addAll(caffeineCacheManager.getCacheNames());
        names.addAll(redisCacheManager.getCacheNames());
        return names;
    }
}
