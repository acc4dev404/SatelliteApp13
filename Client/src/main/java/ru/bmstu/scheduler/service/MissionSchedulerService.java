package ru.bmstu.scheduler.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.bmstu.scheduler.client.SpaceOperationClient;
import ru.bmstu.scheduler.kafka.SatelliteEventConsumer;
import ru.bmstu.scheduler.properties.SpaceCenterProperties;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Сервис планирования миссий.
 * <p>
 * При старте приложения читает конфигурацию из application.yml
 * и регистрирует задачи в TaskScheduler по cron-расписанию.
 * </p>
 */
@Slf4j
@Service
public class MissionSchedulerService {

    private final SpaceCenterProperties properties;
    private final SpaceOperationClient client;
    private final TaskScheduler taskScheduler;
    private final RestClient restClient;
    private final SatelliteEventConsumer satelliteEventConsumer;

    private final Map<String, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();

    public MissionSchedulerService(SpaceCenterProperties properties,
                                   SpaceOperationClient client,
                                   TaskScheduler taskScheduler,
                                   RestClient spaceOperationRestClient,
                                   SatelliteEventConsumer satelliteEventConsumer) {
        this.properties = properties;
        this.client = client;
        this.taskScheduler = taskScheduler;
        this.restClient = spaceOperationRestClient;
        this.satelliteEventConsumer = satelliteEventConsumer;
    }

    /**
     * Инициализация планировщика при старте приложения.
     * Регистрирует все миссии из конфигурации.
     */
    @PostConstruct
    public void init() {
        log.info("Инициализация планировщика миссий...");
        log.info("URL основного сервиса: {}", properties.url());

        if (properties.missions() == null || properties.missions().isEmpty()) {
            log.warn("Нет запланированных миссий в конфигурации");
            return;
        }

        for (SpaceCenterProperties.MissionConfig mission : properties.missions()) {
            registerMission(mission);
        }

        log.info("Зарегистрировано {} миссий", properties.missions().size());
    }

    /**
     * Остановка всех задач при завершении приложения.
     */
    @PreDestroy
    public void shutdown() {
        log.info("Остановка планировщика миссий...");
        activeTasks.forEach((key, future) -> {
            future.cancel(false);
            log.info("Отменена задача: {}", key);
        });
        activeTasks.clear();
        log.info("Планировщик остановлен");
    }

    /**
     * Регистрирует одну миссию в планировщике.
     *
     * @param mission конфигурация миссии
     */
    private void registerMission(SpaceCenterProperties.MissionConfig mission) {
        if (!validateMission(mission)) {
            return;
        }

        if ("SINGLE_SATELLITE".equalsIgnoreCase(mission.targetType())) {

            if (!satelliteEventConsumer.isSatelliteExists(mission.constellationName(), mission.satelliteName())) {
                log.warn("Спутник {} не найден в кэше, миссия не будет запланирована",
                        mission.satelliteName());
                return;
            }
            registerSingleSatelliteMission(mission);
        } else if ("CONSTELLATION".equalsIgnoreCase(mission.targetType())) {
            registerConstellationMission(mission);
        } else {
            log.error("Пропущена миссия: неизвестный targetType '{}'", mission.targetType());
        }
    }

    /**
     * Валидация конфигурации миссии.
     *
     * @param mission конфигурация миссии
     * @return true если валидация пройдена
     */
    private boolean validateMission(SpaceCenterProperties.MissionConfig mission) {
        if (mission.targetType() == null || mission.targetType().isEmpty()) {
            log.error("Пропущена миссия: не указан targetType");
            return false;
        }

        if (mission.constellationName() == null || mission.constellationName().isEmpty()) {
            log.error("Пропущена миссия: не указан constellationName");
            return false;
        }

        if (mission.cron() == null || mission.cron().isEmpty()) {
            log.error("Пропущена миссия: не указан cron для {}", mission.constellationName());
            return false;
        }

        if ("SINGLE_SATELLITE".equalsIgnoreCase(mission.targetType())) {
            if (mission.satelliteName() == null || mission.satelliteName().isEmpty()) {
                log.error("Пропущена миссия: для SINGLE_SATELLITE необходимо указать satelliteName");
                return false;
            }
        }

        return true;
    }

    /**
     * Регистрирует миссию для всей группировки.
     */
    private void registerConstellationMission(SpaceCenterProperties.MissionConfig mission) {
        String taskKey = "constellation_" + mission.constellationName();

        Runnable task = () -> {
            log.info("[{}] Запуск запланированной миссии для группировки: {}",
                    getCurrentTime(), mission.constellationName());
            client.executeConstellationMission(mission.constellationName(), true);
        };

        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(mission.cron()));
        activeTasks.put(taskKey, future);

        log.info("Запланирована миссия для группировки '{}' по расписанию: {}",
                mission.constellationName(), mission.cron());
    }

    /**
     * Регистрирует миссию для конкретного спутника.
     */
    private void registerSingleSatelliteMission(SpaceCenterProperties.MissionConfig mission) {
        String taskKey = "satellite_" + mission.constellationName() + "_" + mission.satelliteName();

        Runnable task = () -> {
            log.info("🕐 [{}] Запуск запланированной миссии для спутника: {}/{}",
                    getCurrentTime(), mission.constellationName(), mission.satelliteName());
            client.executeSingleSatelliteMission(mission.constellationName(), mission.satelliteName(), true);
        };

        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(mission.cron()));
        activeTasks.put(taskKey, future);

        log.info("📅 Запланирована миссия для спутника '{}/{}' по расписанию: {}",
                mission.constellationName(), mission.satelliteName(), mission.cron());
    }

    /**
     * Ручной запуск миссии для группировки.
     *
     * @param constellationName название группировки
     * @param activateBeforeMission активировать ли спутники
     * @return true если успешно
     */
    public boolean runConstellationMissionNow(String constellationName, boolean activateBeforeMission) {
        log.info("Ручной запуск миссии для группировки: {}", constellationName);
        return client.executeConstellationMission(constellationName, activateBeforeMission);
    }

    /**
     * Ручной запуск миссии для спутника.
     *
     * @param constellationName название группировки
     * @param satelliteName имя спутника
     * @param activateBeforeMission активировать ли спутник
     * @return true если успешно
     */
    public boolean runSatelliteMissionNow(String constellationName, String satelliteName, boolean activateBeforeMission) {
        log.info("Ручной запуск миссии для спутника: {}/{}", constellationName, satelliteName);
        return client.executeSingleSatelliteMission(constellationName, satelliteName, activateBeforeMission);
    }

    /**
     * Проверяет доступность основного сервиса.
     *
     * @return true если сервис доступен
     */
    public boolean isMainServiceReachable() {
        try {
            restClient.get()
                    .uri("/actuator/health")
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            log.warn("Основной сервис недоступен: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает текущее время в формате HH:MM:SS.
     */
    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}