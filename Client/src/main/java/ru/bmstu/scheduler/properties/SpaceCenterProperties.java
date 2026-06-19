package ru.bmstu.scheduler.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Конфигурационные свойства для подключения к сервису управления спутниками.
 *
 * @param url     базовый URL основного сервиса
 * @param missions список запланированных миссий
 */
@ConfigurationProperties(prefix = "app.space-center-service")
public record SpaceCenterProperties(
        String url,
        List<MissionConfig> missions
) {
    /**
     * Конфигурация одной запланированной миссии.
     *
     * @param targetType        тип миссии (CONSTELLATION или SINGLE_SATELLITE)
     * @param constellationName название группировки
     * @param satelliteName     имя спутника (обязательно для SINGLE_SATELLITE)
     * @param cron              cron-выражение для расписания
     */
    public record MissionConfig(
            String targetType,
            String constellationName,
            String satelliteName,
            String cron
    ) {}
}