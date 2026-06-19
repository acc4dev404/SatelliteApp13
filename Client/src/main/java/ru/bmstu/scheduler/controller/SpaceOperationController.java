package ru.bmstu.scheduler.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.scheduler.properties.SpaceCenterProperties;
import ru.bmstu.scheduler.service.MissionSchedulerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST-контроллер для управления планировщиком миссий.
 * <p>
 * Предоставляет API для просмотра, добавления и удаления запланированных миссий,
 * а также для ручного запуска миссий.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/scheduler")
@Tag(name = "Mission Scheduler", description = "API для управления планировщиком миссий")
public class SpaceOperationController {

    private final MissionSchedulerService schedulerService;
    private final SpaceCenterProperties properties;

    public SpaceOperationController(MissionSchedulerService schedulerService,
                                    SpaceCenterProperties properties) {
        this.schedulerService = schedulerService;
        this.properties = properties;
    }

    /**
     * Возвращает список всех запланированных миссий из конфигурации.
     *
     * @return список конфигураций миссий
     */
    @GetMapping("/missions")
    @Operation(summary = "Получить список миссий", description = "Возвращает список всех запланированных миссий из конфигурации")
    @ApiResponse(responseCode = "200", description = "Список миссий успешно получен")
    public ResponseEntity<List<SpaceCenterProperties.MissionConfig>> getScheduledMissions() {
        List<SpaceCenterProperties.MissionConfig> missions = properties.missions();
        if (missions == null || missions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(missions);
    }

    /**
     * Возвращает информацию о конкретной миссии.
     *
     * @param index индекс миссии в списке
     * @return конфигурация миссии
     */
    @GetMapping("/missions/{index}")
    @Operation(summary = "Получить миссию по индексу", description = "Возвращает конфигурацию миссии по указанному индексу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Миссия найдена"),
            @ApiResponse(responseCode = "404", description = "Миссия не найдена")
    })
    public ResponseEntity<SpaceCenterProperties.MissionConfig> getMissionByIndex(
            @Parameter(description = "Индекс миссии в списке") @PathVariable int index) {
        List<SpaceCenterProperties.MissionConfig> missions = properties.missions();
        if (missions == null || index < 0 || index >= missions.size()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(missions.get(index));
    }

    /**
     * Ручной запуск миссии для группировки.
     *
     * @param constellationName название группировки
     * @param activateBeforeMission активировать ли спутники перед миссией
     * @return статус выполнения
     */
    @PostMapping("/missions/constellation/{constellationName}/run")
    @Operation(summary = "Запустить миссию группировки", description = "Ручной запуск миссии для указанной группировки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Миссия успешно запущена"),
            @ApiResponse(responseCode = "500", description = "Ошибка при выполнении миссии")
    })
    public ResponseEntity<Map<String, String>> runConstellationMission(
            @Parameter(description = "Название группировки") @PathVariable String constellationName,
            @RequestParam(defaultValue = "true") boolean activateBeforeMission) {

        log.info("🕐 Ручной запуск миссии для группировки: {}", constellationName);

        boolean success = schedulerService.runConstellationMissionNow(constellationName, activateBeforeMission);

        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("status", "success");
            response.put("message", "Миссия для группировки " + constellationName + " успешно выполнена");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Ошибка при выполнении миссии для группировки " + constellationName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Ручной запуск миссии для конкретного спутника.
     *
     * @param constellationName название группировки
     * @param satelliteName имя спутника
     * @param activateBeforeMission активировать ли спутник перед миссией
     * @return статус выполнения
     */
    @PostMapping("/missions/satellite/{constellationName}/{satelliteName}/run")
    @Operation(summary = "Запустить миссию спутника", description = "Ручной запуск миссии для указанного спутника")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Миссия успешно запущена"),
            @ApiResponse(responseCode = "500", description = "Ошибка при выполнении миссии")
    })
    public ResponseEntity<Map<String, String>> runSatelliteMission(
            @Parameter(description = "Название группировки") @PathVariable String constellationName,
            @Parameter(description = "Имя спутника") @PathVariable String satelliteName,
            @RequestParam(defaultValue = "true") boolean activateBeforeMission) {

        log.info("🕐 Ручной запуск миссии для спутника: {}/{}", constellationName, satelliteName);

        boolean success = schedulerService.runSatelliteMissionNow(constellationName, satelliteName, activateBeforeMission);

        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("status", "success");
            response.put("message", "Миссия для спутника " + satelliteName + " успешно выполнена");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Ошибка при выполнении миссии для спутника " + satelliteName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Получает статистику работы планировщика.
     *
     * @return статистика выполнения миссий
     */
    @GetMapping("/stats")
    @Operation(summary = "Статистика планировщика", description = "Возвращает статистику выполнения миссий")
    @ApiResponse(responseCode = "200", description = "Статистика успешно получена")
    public ResponseEntity<Map<String, Object>> getSchedulerStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMissionsConfigured", properties.missions() != null ? properties.missions().size() : 0);
        stats.put("schedulerRunning", true);
        stats.put("missionsByType", getMissionsByType());

        return ResponseEntity.ok(stats);
    }

    /**
     * Группирует миссии по типу для статистики.
     *
     * @return карта с количеством миссий каждого типа
     */
    private Map<String, Long> getMissionsByType() {
        if (properties.missions() == null) {
            return Map.of();
        }
        return properties.missions().stream()
                .collect(Collectors.groupingBy(
                        SpaceCenterProperties.MissionConfig::targetType,
                        Collectors.counting()
                ));
    }

    /**
     * Проверяет доступность основного сервиса (health check).
     *
     * @return статус доступности
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Проверяет доступность основного сервиса")
    @ApiResponse(responseCode = "200", description = "Сервис доступен")
    public ResponseEntity<Map<String, String>> healthCheck() {
        boolean isReachable = schedulerService.isMainServiceReachable();

        Map<String, String> health = new HashMap<>();
        health.put("status", isReachable ? "UP" : "DOWN");
        health.put("service", "mission-scheduler");
        health.put("mainServiceUrl", properties.url());

        if (!isReachable) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
        return ResponseEntity.ok(health);
    }
}