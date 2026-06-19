package ru.bmstu.scheduler.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.bmstu.scheduler.dto.MissionRequest;
import ru.bmstu.scheduler.dto.SingleSatelliteMissionRequest;

import java.util.Collections;

/**
 * HTTP-клиент для взаимодействия с основным сервисом управления спутниками.
 */
@Slf4j
@Component
public class SpaceOperationClient {

    private final RestClient restClient;

    public SpaceOperationClient(RestClient spaceOperationRestClient) {
        this.restClient = spaceOperationRestClient;
    }

    /**
     * Выполняет миссию для всей группировки.
     *
     * @param constellationName название группировки
     * @param activateBeforeMission активировать ли спутники перед миссией
     * @return true если запрос успешен, false в противном случае
     */
    public boolean executeConstellationMission(String constellationName, boolean activateBeforeMission) {
        try {
            MissionRequest request = new MissionRequest(
                    Collections.singletonList(constellationName),
                    activateBeforeMission,
                    true  // показывать статус после миссии
            );

            log.info("📡 Отправка запроса на выполнение миссии для группировки: {}", constellationName);

            restClient.post()
                    .uri("/missions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            log.info("✅ Миссия для группировки {} успешно выполнена", constellationName);
            return true;

        } catch (Exception e) {
            log.error("❌ Ошибка при выполнении миссии для группировки {}: {}", constellationName, e.getMessage());
            return false;
        }
    }

    /**
     * Выполняет миссию для конкретного спутника.
     *
     * @param constellationName название группировки
     * @param satelliteName     имя спутника
     * @param activateBeforeMission активировать ли спутник перед миссией
     * @return true если запрос успешен, false в противном случае
     */
    public boolean executeSingleSatelliteMission(String constellationName, String satelliteName, boolean activateBeforeMission) {
        try {
            SingleSatelliteMissionRequest request = new SingleSatelliteMissionRequest(
                    constellationName,
                    satelliteName,
                    activateBeforeMission
            );

            log.info("📡 Отправка запроса на выполнение миссии для спутника: {}/{}", constellationName, satelliteName);

            restClient.post()
                    .uri("/missions/satellite")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            log.info("✅ Миссия для спутника {}/{} успешно выполнена", constellationName, satelliteName);
            return true;

        } catch (Exception e) {
            log.error("❌ Ошибка при выполнении миссии для спутника {}/{}: {}", constellationName, satelliteName, e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет доступность основного сервиса.
     *
     * @return true если сервис доступен
     */
    public boolean isMainServiceAvailable() {
        try {
            restClient.get()
                    .uri("/actuator/health")
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}