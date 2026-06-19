package ru.bmstu.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bmstu.entity.CommunicationSatelliteEntity;
import ru.bmstu.entity.ConstellationEntity;
import ru.bmstu.entity.ImagingSatelliteEntity;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.repository.ConstellationRepository;
import ru.bmstu.repository.SatelliteRepository;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Интеграционные тесты ConstellationService с PostgreSQL")
class ConstellationServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ConstellationService constellationService;

    @Autowired
    private ConstellationRepository constellationRepository;

    @Autowired
    private SatelliteRepository satelliteRepository;

    private static final String CONSTELLATION_NAME = "Тест-группировка";
    private static final String COMM_SAT_NAME = "Тест-Связь";
    private static final String IMG_SAT_NAME = "Тест-ДЗЗ";

    @Test
    @DisplayName("Создание группировки и добавление спутников")
    void createConstellationAndAddSatellites_ShouldWork() {
        // 1. Создаём группировку
        constellationService.createAndSaveConstellation(CONSTELLATION_NAME);

        // Проверка
        assertTrue(constellationRepository.existsByName(CONSTELLATION_NAME));

        ConstellationEntity constellation = constellationRepository.findByName(CONSTELLATION_NAME).orElseThrow();
        assertEquals(0, constellation.getSatellites().size());

        // 2. Создаём и добавляем спутники
        SatelliteEntity commSat = new CommunicationSatelliteEntity(COMM_SAT_NAME, 0.85, 500);
        SatelliteEntity imgSat = new ImagingSatelliteEntity(IMG_SAT_NAME, 0.75, 2.5);

        constellationService.addSatelliteToConstellation(CONSTELLATION_NAME, commSat);
        constellationService.addSatelliteToConstellation(CONSTELLATION_NAME, imgSat);

        // Проверка
        ConstellationEntity updatedConstellation = constellationRepository.findByName(CONSTELLATION_NAME).orElseThrow();
        assertEquals(2, updatedConstellation.getSatellites().size());
    }

    @Test
    @DisplayName("Активация и выполнение миссий")
    void activateAndExecuteMissions_ShouldUpdateState() {
        // Создаём группировку со спутниками
        constellationService.createAndSaveConstellation(CONSTELLATION_NAME);

        SatelliteEntity commSat = new CommunicationSatelliteEntity(COMM_SAT_NAME, 0.85, 500);
        SatelliteEntity imgSat = new ImagingSatelliteEntity(IMG_SAT_NAME, 0.75, 2.5);

        constellationService.addSatelliteToConstellation(CONSTELLATION_NAME, commSat);
        constellationService.addSatelliteToConstellation(CONSTELLATION_NAME, imgSat);

        double initialCommBattery = commSat.getEnergy().getBatteryLevel();
        double initialImgBattery = imgSat.getEnergy().getBatteryLevel();

        // Активация
        constellationService.activateAllSatellites(CONSTELLATION_NAME);

        // Проверка активации
        assertTrue(commSat.getState().getIsActive());
        assertTrue(imgSat.getState().getIsActive());

        // Выполнение миссий
        constellationService.executeConstellationMission(CONSTELLATION_NAME);

        // Проверка расхода энергии
        assertTrue(commSat.getEnergy().getBatteryLevel() < initialCommBattery);
        assertTrue(imgSat.getEnergy().getBatteryLevel() < initialImgBattery);

        // У ImagingSatellite должны увеличиться счётчики фото
        assertTrue(((ImagingSatelliteEntity) imgSat).getPhotosTaken() > 0);
    }
}