package music_service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.concurrent.TimeUnit;

/**
 * Using Caffeine CacheManager
 */
@Configuration
@EnableCaching
public class CacheManagerConfig {
    @Bean
    Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(100);
    }

    @Bean
    CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

//    @Bean
//    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(10)); // Set TTL for Redis cache
//
//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(redisCacheConfiguration)
//                .build();
//    }
//
//    @Bean
//    public CacheManager compositeCacheManager(Caffeine<Object, Object> caffeine,
//                                              RedisConnectionFactory redisConnectionFactory) {
//        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(
//                caffeineCacheManager(caffeine), redisCacheManager(redisConnectionFactory));
//        compositeCacheManager.setFallbackToNoOpCache(true); // Optional: Avoid cache misses if no manager handles it
//        return compositeCacheManager;
//    }
}
