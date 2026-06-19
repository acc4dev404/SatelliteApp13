package ru.bmstu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.annotation.LogExecutionTime;
import ru.bmstu.dto.ConstellationStatusResponse;
import ru.bmstu.exception.SpaceOperationException;
import ru.bmstu.entity.ConstellationEntity;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.repository.ConstellationRepository;
import ru.bmstu.repository.SatelliteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ConstellationService {

    private final ConstellationRepository constellationRepository;
    private final SatelliteRepository satelliteRepository;
    private static final Logger log = LoggerFactory.getLogger(ConstellationService.class);

    public ConstellationService(ConstellationRepository constellationRepository,
                                SatelliteRepository satelliteRepository) {
        this.constellationRepository = constellationRepository;
        this.satelliteRepository = satelliteRepository;
    }

    @Transactional(readOnly = true)
    public ConstellationStatusResponse getConstellationStatus(String constellationName) {
        log.debug("ЗАПРОС В БД (НЕ ИЗ КЭША) - статус группировки: {}", constellationName);

        ConstellationEntity constellation = constellationRepository.findByName(constellationName)
                .orElseThrow(() -> new SpaceOperationException("Группировка не найдена: " + constellationName));

        List<SatelliteEntity> satellites = constellation.getSatellites();

        Map<String, String> statuses = satellites.stream()
                .collect(Collectors.toMap(
                        SatelliteEntity::getName,
                        s -> s.getState().getStatusMessage(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        Map<String, Double> batteryLevels = satellites.stream()
                .collect(Collectors.toMap(
                        SatelliteEntity::getName,
                        s -> s.getEnergy().getBatteryLevel(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        return new ConstellationStatusResponse(
                constellationName,
                satellites.size(),
                statuses,
                batteryLevels
        );
    }

    @Transactional(readOnly = true)
    public Map<String, ConstellationStatusResponse> getAllConstellationsStatus() {
        log.debug("Запрос к БД для статуса всех группировок");

        Map<String, ConstellationStatusResponse> result = new LinkedHashMap<>();

        for (ConstellationEntity constellation : constellationRepository.findAll()) {
            String name = constellation.getName();
            List<SatelliteEntity> satellites = constellation.getSatellites();

            Map<String, String> statuses = satellites.stream()
                    .collect(Collectors.toMap(
                            SatelliteEntity::getName,
                            s -> s.getState().getStatusMessage(),
                            (existing, replacement) -> existing,
                            LinkedHashMap::new
                    ));

            Map<String, Double> batteryLevels = satellites.stream()
                    .collect(Collectors.toMap(
                            SatelliteEntity::getName,
                            s -> s.getEnergy().getBatteryLevel(),
                            (existing, replacement) -> existing,
                            LinkedHashMap::new
                    ));

            result.put(name, new ConstellationStatusResponse(
                    name,
                    satellites.size(),
                    statuses,
                    batteryLevels
            ));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public ConstellationEntity getConstellation(String name) {
        log.debug("Запрос к БД для группировки: {}", name);
        return constellationRepository.findByName(name)
                .orElseThrow(() -> new SpaceOperationException("Группировка не найдена: " + name));
    }

    @Transactional(readOnly = true)
    public Map<String, ConstellationEntity> getAllConstellations() {
        log.debug("Запрос к БД для всех группировок");
        return constellationRepository.findAll().stream()
                .collect(Collectors.toMap(
                        ConstellationEntity::getName,
                        c -> c
                ));
    }

    @CacheEvict(value = {"constellationsAll", "constellationStatus", "satellitesAll"}, allEntries = true)
    @Transactional
    public ConstellationEntity createConstellation(String name) {
        Optional<ConstellationEntity> existing = constellationRepository.findByName(name);
        if (existing.isPresent()) {
            log.warn("Группировка с именем {} уже существует", name);
            return existing.get();
        }

        ConstellationEntity constellation = new ConstellationEntity();
        constellation.setName(name);
        ConstellationEntity saved = constellationRepository.save(constellation);
        log.info("Создана группировка: {}", name);
        return saved;
    }

    @CacheEvict(value = {"constellationsAll", "constellationStatus", "satellitesAll"}, allEntries = true)
    @Transactional
    public void createAndSaveConstellation(String name) {
        if (constellationRepository.existsByName(name)) {
            throw new IllegalArgumentException("Группировка с именем '" + name + "' уже существует");
        }
        ConstellationEntity constellation = new ConstellationEntity(name);
        constellationRepository.save(constellation);
        log.info("Сохранена группировка: {}", name);
    }

    @CacheEvict(value = {"constellationsAll", "constellationStatus", "satellitesAll"}, allEntries = true)
    @Transactional
    public void addSatelliteToConstellation(String constellationName, SatelliteEntity satelliteEntity) {
        ConstellationEntity constellation = getConstellation(constellationName);

        if (satelliteEntity == null) {
            throw new SpaceOperationException("Спутник не может быть null");
        }

        constellation.addSatellite(satelliteEntity);
        constellationRepository.save(constellation);
        log.info("Добавлен спутник {} в группировку {}", satelliteEntity.getName(), constellationName);
    }

    @CacheEvict(value = {"constellationsAll", "constellationStatus", "satellitesAll"}, allEntries = true)
    @Transactional
    public boolean removeSatelliteFromConstellation(String constellationName, String satelliteName) {
        ConstellationEntity constellation = getConstellation(constellationName);

        boolean removed = constellation.removeSatellite(satelliteName);
        if (removed) {
            constellationRepository.save(constellation);
            log.info("Удалён спутник {} из группировки {}", satelliteName, constellationName);
        }
        return removed;
    }

    @CacheEvict(value = {"constellationsAll", "constellationStatus", "satellitesAll"}, allEntries = true)
    @Transactional
    public void deleteConstellation(String name) {
        ConstellationEntity constellation = getConstellation(name);
        constellationRepository.delete(constellation);
        log.info("Удалена группировка: {}", name);
    }

    @LogExecutionTime(threshold = 100)
    @Transactional
    public void executeConstellationMission(String constellationName) {
        ConstellationEntity constellation = getConstellation(constellationName);

        System.out.println("\n=== ВЫПОЛНЕНИЕ МИССИЙ ДЛЯ ГРУППИРОВКИ: " + constellationName + " ===");
        System.out.println("=".repeat(50));

        for (SatelliteEntity satellite : constellation.getSatellites()) {
            satellite.performMissionLogic();
            satelliteRepository.save(satellite);
        }
    }

    @LogExecutionTime
    @Transactional
    public void activateAllSatellites(String constellationName) {
        ConstellationEntity constellation = getConstellation(constellationName);

        System.out.println("\n=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: " + constellationName + " ===");

        for (SatelliteEntity satellite : constellation.getSatellites()) {
            satellite.getState().activate(satellite.getEnergy().hasSufficientPower());
            satelliteRepository.save(satellite);
        }
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public void showConstellationStatus(String constellationName) {
        ConstellationEntity constellation = getConstellation(constellationName);

        System.out.println("\n=== СТАТУС ГРУППИРОВКИ: " + constellationName + " ===");

        List<SatelliteEntity> satellites = constellation.getSatellites();
        System.out.println("Количество спутников: " + satellites.size());

        for (SatelliteEntity satellite : satellites) {
            System.out.println("  " + satellite.getName() + ": " + satellite.getState().getStatusMessage() +
                    " (заряд: " + (int)(satellite.getEnergy().getBatteryLevel() * 100) + "%)");
        }
    }

    public SatelliteEntity getSatelliteByName(String constellationName, String satelliteName) {
        ConstellationEntity constellation = getConstellation(constellationName);

        return constellation.getSatellites().stream()
                .filter(s -> s.getName().equals(satelliteName))
                .findFirst()
                .orElse(null);
    }

    @CacheEvict(value = "satellitesAll", allEntries = true)
    public void evictSatellitesAllCache() {
        log.debug("Инвалидирован кэш satellitesAll из ConstellationService");
    }
}