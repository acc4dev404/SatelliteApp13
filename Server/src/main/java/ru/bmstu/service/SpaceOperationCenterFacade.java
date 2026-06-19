package ru.bmstu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.annotation.LogExecutionTime;
import ru.bmstu.dto.AddSatelliteRequest;
import ru.bmstu.dto.ConstellationStatusResponse;
import ru.bmstu.dto.CreateConstellationRequest;
import ru.bmstu.dto.MissionRequest;
import ru.bmstu.entity.CommunicationSatelliteEntity;
import ru.bmstu.entity.ImagingSatelliteEntity;
import ru.bmstu.entity.OutboxEntity;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.kafka.SatelliteEvent;
import ru.bmstu.param.SatelliteParam;
import ru.bmstu.repository.OutboxRepository;
import ru.bmstu.repository.SatelliteRepository;
import ru.bmstu.repository.ConstellationRepository;
import ru.bmstu.service.satellite.ISatelliteService;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpaceOperationCenterFacade {

    private static final Logger log = LoggerFactory.getLogger(SpaceOperationCenterFacade.class);

    private final ConstellationService constellationService;
    private final ISatelliteService satelliteService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final SatelliteRepository satelliteRepository;
    private final ConstellationRepository constellationRepository;

    @Lazy
    @Autowired
    private SpaceOperationCenterFacade self;

    public SpaceOperationCenterFacade(ConstellationService constellationService,
                                      ISatelliteService satelliteService,
                                      OutboxRepository outboxRepository,
                                      ObjectMapper objectMapper,
                                      SatelliteRepository satelliteRepository,
                                      ConstellationRepository constellationRepository) {
        this.constellationService = constellationService;
        this.satelliteService = satelliteService;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.satelliteRepository = satelliteRepository;
        this.constellationRepository = constellationRepository;
    }

    public Map<String, ConstellationStatusResponse> getAllConstellationsStatus() {
        return self.getCachedAllConstellationsStatus();
    }

    public ConstellationStatusResponse getConstellationStatus(String constellationName) {
        return self.getCachedConstellationStatus(constellationName);
    }

    @Cacheable(value = "constellationsAll", key = "'all'")
    public Map<String, ConstellationStatusResponse> getCachedAllConstellationsStatus() {
        log.debug("ЗАПРОС В БД (НЕ ИЗ КЭША) - статус всех группировок");
        return constellationService.getAllConstellationsStatus();
    }

    @Cacheable(value = "constellationStatus", key = "#constellationName")
    public ConstellationStatusResponse getCachedConstellationStatus(String constellationName) {
        log.debug("ЗАПРОС В БД (НЕ ИЗ КЭША) - статус группировки: {}", constellationName);
        return constellationService.getConstellationStatus(constellationName);
    }

    @LogExecutionTime(threshold = 200)
    @Transactional
    public String createConstellationWithSatellites(CreateConstellationRequest request) {
        String constellationName = request.getConstellationName();

        constellationService.createAndSaveConstellation(constellationName);

        List<SatelliteParam> satelliteParams = request.getSatelliteParams();
        for (SatelliteParam param : satelliteParams) {
            SatelliteEntity satellite = satelliteService.createSatellite(param);
            constellationService.addSatelliteToConstellation(constellationName, satellite);

            if (satellite.getId() == null) {
                satellite = satelliteRepository.save(satellite);
            }
            saveToOutbox(satellite, "SATELLITE_CREATED");
        }

        System.out.println("Группировка '" + constellationName + "' успешно создана с " +
                satelliteParams.size() + " спутниками");

        return constellationName;
    }

    @LogExecutionTime
    @Transactional
    public SatelliteEntity addSatelliteToConstellation(AddSatelliteRequest request) {
        SatelliteEntity satellite = satelliteService.createSatellite(request.getSatelliteParam());

        constellationService.addSatelliteToConstellation(
                request.getConstellationName(),
                satellite
        );

        if (satellite.getId() == null) {
            satellite = satelliteRepository.save(satellite);
        }
        saveToOutbox(satellite, "SATELLITE_CREATED");

        return satellite;
    }

    @LogExecutionTime(threshold = 500)
    @Transactional
    public void executeMissions(MissionRequest request) {
        List<String> constellationNames = request.getConstellationNames();

        for (String constellationName : constellationNames) {
            System.out.println("\n🛰️ Обработка группировки: " + constellationName);

            if (request.isActivateBeforeMission()) {
                constellationService.activateAllSatellites(constellationName);
            }

            constellationService.executeConstellationMission(constellationName);

            if (request.isShowStatusAfterMission()) {
                constellationService.showConstellationStatus(constellationName);
            }
        }
    }

    @LogExecutionTime(threshold = 1000)
    @Transactional
    public ConstellationStatusResponse runFullMissionCycle(CreateConstellationRequest request) {
        String constellationName = createConstellationWithSatellites(request);

        constellationService.activateAllSatellites(constellationName);
        constellationService.executeConstellationMission(constellationName);

        return constellationService.getConstellationStatus(constellationName);
    }

    @LogExecutionTime
    @Transactional
    public boolean removeSatelliteFromConstellation(String constellationName, String satelliteName) {
        SatelliteEntity satellite = constellationService.getSatelliteByName(constellationName, satelliteName);
        boolean removed = constellationService.removeSatelliteFromConstellation(constellationName, satelliteName);

        if (removed && satellite != null && satellite.getId() != null) {
            saveToOutbox(satellite, "SATELLITE_DELETED");
        }
        return removed;
    }

    public boolean hasAnyConstellation() {
        return constellationRepository.count() > 0;
    }

    private void saveToOutbox(SatelliteEntity satellite, String eventType) {
        try {

            if (satellite.getId() == null) {
                log.error("Нельзя сохранить событие в outbox: satellite.id = null для спутника {}", satellite.getName());
                return;
            }

            SatelliteEvent event = buildSatelliteEvent(satellite,
                    eventType.equals("SATELLITE_CREATED")
                            ? SatelliteEvent.EventType.SATELLITE_CREATED
                            : SatelliteEvent.EventType.SATELLITE_DELETED);

            String payload = objectMapper.writeValueAsString(event);
            OutboxEntity outbox = new OutboxEntity(satellite.getId(), eventType, payload);
            outboxRepository.save(outbox);
            log.info("Событие {} сохранено в outbox для спутника {} (id={})",
                    eventType, satellite.getName(), satellite.getId());
        } catch (Exception e) {
            log.error("Ошибка сохранения события в outbox: {}", e.getMessage(), e);
        }
    }

    private SatelliteEvent buildSatelliteEvent(SatelliteEntity satellite, SatelliteEvent.EventType eventType) {
        SatelliteEvent.SatelliteEventBuilder builder = SatelliteEvent.builder()
                .eventType(eventType)
                .satelliteId(satellite.getId())
                .name(satellite.getName())
                .constellationName(satellite.getConstellation() != null ?
                        satellite.getConstellation().getName() : null)
                .batteryLevel(satellite.getEnergy().getBatteryLevel())
                .timestamp(Instant.now());

        if (satellite instanceof CommunicationSatelliteEntity comm) {
            builder.satelliteType("COMMUNICATION")
                    .bandwidth(comm.getBandwidth());
        } else if (satellite instanceof ImagingSatelliteEntity img) {
            builder.satelliteType("IMAGING")
                    .resolution(img.getResolution());
        }

        return builder.build();
    }
}