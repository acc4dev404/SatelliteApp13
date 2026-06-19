package ru.bmstu.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.bmstu.service.CacheMetricsService;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
                                     CacheMetricsService cacheMetricsService) {
        log.info("CacheConfig: Инициализация cacheManager с Redis...");

        try {
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                    .entryTtl(Duration.ofMinutes(10))
                    .disableCachingNullValues();

            Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
            cacheConfigurations.put("satellite", defaultConfig.entryTtl(Duration.ofMinutes(10)));
            cacheConfigurations.put("constellation", defaultConfig.entryTtl(Duration.ofMinutes(15)));
            cacheConfigurations.put("constellationStatus", defaultConfig.entryTtl(Duration.ofMinutes(15)));
            cacheConfigurations.put("satellitesAll", defaultConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("constellationsAll", defaultConfig.entryTtl(Duration.ofMinutes(5)));

            RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(defaultConfig)
                    .build();

            cacheMetricsService.registerMetrics();

            log.info("CacheConfig: RedisCacheManager создан УСПЕШНО!");
            return cacheManager;

        } catch (Exception e) {
            log.error("CacheConfig: Ошибка при создании RedisCacheManager, используем in-memory кэш: {}", e.getMessage());
            return new ConcurrentMapCacheManager();
        }
    }
}