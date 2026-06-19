package ru.bmstu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.dto.*;
import ru.bmstu.service.SpaceOperationCenterFacade;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Space Operation Center", description = "API для управления спутниковыми группировками")
public class SpaceOperationController {

    private final SpaceOperationCenterFacade facade;

    public SpaceOperationController(SpaceOperationCenterFacade facade) {
        this.facade = facade;
    }

    // ==================== GET ЭНДПОИНТЫ ====================

    @GetMapping("/constellations/status")
    @Operation(summary = "Получить статус всех группировок")
    public ResponseEntity<Map<String, ConstellationStatusResponse>> getAllConstellationsStatus() {
        return ResponseEntity.ok(facade.getAllConstellationsStatus());
    }

    @GetMapping("/constellations/{name}/status")
    @Operation(summary = "Получить статус конкретной группировки")
    public ResponseEntity<ConstellationStatusResponse> getConstellationStatus(
            @PathVariable String name) {
        return ResponseEntity.ok(facade.getConstellationStatus(name));
    }

    @GetMapping("/overview")
    @Operation(summary = "Получить сводку о состоянии системы")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, ConstellationStatusResponse> allStatuses = facade.getAllConstellationsStatus();

        long totalSatellites = allStatuses.values().stream()
                .mapToInt(ConstellationStatusResponse::getSatelliteCount)
                .sum();

        long activeSatellites = allStatuses.values().stream()
                .flatMap(status -> status.getSatelliteStatuses().values().stream())
                .filter("Активен"::equals)
                .count();

        long criticalBattery = allStatuses.values().stream()
                .flatMap(status -> status.getBatteryLevels().values().stream())
                .filter(level -> level < 0.2)
                .count();

        return ResponseEntity.ok(Map.of(
                "totalConstellations", allStatuses.size(),
                "totalSatellites", totalSatellites,
                "activeSatellites", activeSatellites,
                "inactiveSatellites", totalSatellites - activeSatellites,
                "criticalBatterySatellites", criticalBattery,
                "constellations", allStatuses.keySet()
        ));
    }

    // ==================== POST ЭНДПОИНТЫ ====================

    @PostMapping("/constellations")
    @Operation(summary = "Создать новую группировку со спутниками")
    public ResponseEntity<Map<String, String>> createConstellation(
            @RequestBody CreateConstellationRequest request) {
        String name = facade.createConstellationWithSatellites(request);
        return ResponseEntity.ok(Map.of(
                "constellationName", name,
                "message", "Группировка успешно создана"
        ));
    }

    @PostMapping("/constellations/satellites")
    @Operation(summary = "Добавить спутник в существующую группировку")
    public ResponseEntity<Map<String, String>> addSatellite(
            @RequestBody AddSatelliteRequest request) {
        facade.addSatelliteToConstellation(request);
        return ResponseEntity.ok(Map.of(
                "message", "Спутник успешно добавлен в группировку " + request.getConstellationName()
        ));
    }

    @PostMapping("/missions/satellite")
    @Operation(summary = "Выполнить миссию для конкретного спутника")
    public ResponseEntity<Void> executeSatelliteMission(@RequestBody SingleSatelliteMissionRequest request) {
        // Создаём MissionRequest для группировки
        MissionRequest missionRequest = new MissionRequest(
                List.of(request.getConstellationName()),
                request.isActivateBeforeMission(),
                true
        );
        facade.executeMissions(missionRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/missions")
    @Operation(summary = "Выполнить миссии для группировок")
    public ResponseEntity<Void> executeMissions(@RequestBody MissionRequest request) {
        facade.executeMissions(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/missions/full-cycle")
    @Operation(summary = "Полный цикл миссии: создание + активация + миссия + статус")
    public ResponseEntity<ConstellationStatusResponse> runFullMissionCycle(
            @RequestBody CreateConstellationRequest request) {
        ConstellationStatusResponse result = facade.runFullMissionCycle(request);
        return ResponseEntity.ok(result);
    }

    // ==================== DELETE ЭНДПОИНТЫ ====================

    @DeleteMapping("/constellations/{constellationName}/satellites/{satelliteName}")
    @Operation(summary = "Удалить спутник из группировки (вывести из эксплуатации)")
    public ResponseEntity<Map<String, Object>> removeSatellite(
            @PathVariable String constellationName,
            @PathVariable String satelliteName) {

        boolean removed = facade.removeSatelliteFromConstellation(constellationName, satelliteName);

        if (removed) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Спутник " + satelliteName + " успешно удален из группировки " + constellationName
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Спутник " + satelliteName + " не найден в группировке " + constellationName
            ));
        }
    }
}