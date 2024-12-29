package media_service.config.cache;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomCache implements Cache {

    Cache caffeineCache;
    Cache redisCache;

    public CustomCache(Cache caffeineCache, Cache redisCache) {
        this.caffeineCache = caffeineCache;
        this.redisCache = redisCache;
    }

    @Override
    public String getName() {
        return caffeineCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return caffeineCache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        System.out.println("In ValueWrapper get(Obj)");
        // First try to get from Caffeine (L1)
        ValueWrapper value = caffeineCache.get(key);
        if (value != null) {
            return value;
        }

        // If not found in L1, try Redis (L2)
        value = redisCache.get(key);
        if (value != null) {
            // Populate L1 cache for faster future access
            System.out.println("Out of L1, fetching from L2");
            caffeineCache.put(key, value.get());
        }

        //test to see if it works
        if(value == null){
            System.out.println("Not in N1 or N2");
        }

        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        System.out.println("In T get(Obj, Class)");

        // First check in Caffeine (L1)
        T value = caffeineCache.get(key, type);
        if (value != null) {
            return value;
        }

        // If not in Caffeine, check in Redis (L2)
        value = redisCache.get(key, type);
        if (value != null) {
            // Populate Caffeine for next access
            caffeineCache.put(key, value);
        }

        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        try {
            // First check in Caffeine (L1)
            return caffeineCache.get(key, () -> {
                // If not in Caffeine, load from Redis (L2)
                T value = redisCache.get(key, valueLoader);
                if (value != null) {
                    // Populate Caffeine for next access
                    caffeineCache.put(key, value);
                }
                return value;
            });
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        caffeineCache.put(key, value);
        redisCache.put(key, value);
    }

    @Override
    public void evict(Object key) {
        caffeineCache.evict(key);
        redisCache.evict(key);
    }

    @Override
    public void clear() {
        caffeineCache.clear();
        redisCache.clear();
    }
}
