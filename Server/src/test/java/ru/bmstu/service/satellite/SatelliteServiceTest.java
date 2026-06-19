package ru.bmstu.service.satellite;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.entity.CommunicationSatelliteEntity;
import ru.bmstu.entity.ImagingSatelliteEntity;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.exception.SpaceOperationException;
import ru.bmstu.param.CommunicationSatelliteParam;
import ru.bmstu.param.ImagingSatelliteParam;
import ru.bmstu.param.SatelliteParam;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Интеграционные тесты для SatelliteService. </p>
 * <p> Проверяет работу сервиса в контексте Spring Boot,
 * создание спутников различных типов, обработку ошибок. </p>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Интеграционные тесты SatelliteService")
class SatelliteServiceTest {

    private static final String SATELLITE_NAME = "Тест-спутник";
    private static final double TEST_BATTERY = 0.8;
    private static final double TEST_BANDWIDTH = 500.0;
    private static final double TEST_RESOLUTION = 2.5;

    @Autowired
    private ISatelliteService satelliteService;

    @Test
    @DisplayName("Сервис должен создавать спутник связи по правильным параметрам")
    void createSatellite_WithCommunicationParam_ShouldCreateCommunicationSatellite() {
        // Arrange
        SatelliteParam param = new CommunicationSatelliteParam(
                SATELLITE_NAME, TEST_BATTERY, TEST_BANDWIDTH
        );

        // Act
        SatelliteEntity satellite = satelliteService.createSatellite(param);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof CommunicationSatelliteEntity);
        assertEquals(SATELLITE_NAME, satellite.getName());
        assertEquals(TEST_BATTERY, satellite.getEnergy().getBatteryLevel());
        assertEquals(TEST_BANDWIDTH, ((CommunicationSatelliteEntity) satellite).getBandwidth());
    }

    @Test
    @DisplayName("Сервис должен создавать спутник ДЗЗ по правильным параметрам")
    void createSatellite_WithImagingParam_ShouldCreateImagingSatellite() {
        // Arrange
        SatelliteParam param = new ImagingSatelliteParam(
                SATELLITE_NAME, TEST_BATTERY, TEST_RESOLUTION
        );

        // Act
        SatelliteEntity satellite = satelliteService.createSatellite(param);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof ImagingSatelliteEntity);
        assertEquals(SATELLITE_NAME, satellite.getName());
        assertEquals(TEST_BATTERY, satellite.getEnergy().getBatteryLevel());
        assertEquals(TEST_RESOLUTION, ((ImagingSatelliteEntity) satellite).getResolution());
    }

    @Test
    @DisplayName("Сервис должен выбрасывать исключение при неизвестном типе параметров")
    void createSatellite_WithUnknownParamType_ShouldThrowException() {
        // Создаем анонимный подкласс для неизвестного типа
        SatelliteParam unknownParam = new SatelliteParam(null, SATELLITE_NAME, TEST_BATTERY) {};

        // Act & Assert
        assertThrows(SpaceOperationException.class,
                () -> satelliteService.createSatellite(unknownParam));
    }
}