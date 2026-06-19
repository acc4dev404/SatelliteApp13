package ru.bmstu.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class CacheMetricsService {

    private final Counter cacheGets;
    private final Counter cachePuts;
    private final Counter cacheEvictions;

    public CacheMetricsService(MeterRegistry meterRegistry) {
        this.cacheGets = Counter.builder("cache.gets")
                .description("Number of cache gets")
                .register(meterRegistry);
        this.cachePuts = Counter.builder("cache.puts")
                .description("Number of cache puts")
                .register(meterRegistry);
        this.cacheEvictions = Counter.builder("cache.evictions")
                .description("Number of cache evictions")
                .register(meterRegistry);
    }

    public void incrementGets() {
        cacheGets.increment();
    }

    public void incrementPuts() {
        cachePuts.increment();
    }

    public void incrementEvictions() {
        cacheEvictions.increment();
    }

    public void registerMetrics() {

    }
}