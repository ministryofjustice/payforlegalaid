package uk.gov.laa.gpfd.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

public class TokenCacheConfig {
    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("tokenCache");
    }
}
