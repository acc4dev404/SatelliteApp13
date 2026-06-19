package ru.bmstu.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.bmstu.entity.CommunicationSatelliteEntity;
import ru.bmstu.entity.ImagingSatelliteEntity;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.exception.SpaceOperationException;
import ru.bmstu.factory.impl.CommunicationSatelliteFactory;
import ru.bmstu.factory.impl.ImagingSatelliteFactory;
import ru.bmstu.param.CommunicationSatelliteParam;
import ru.bmstu.param.ImagingSatelliteParam;
import ru.bmstu.param.SatelliteParam;
import ru.bmstu.param.SatelliteType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Тесты для проверки работы фабрик спутников. </p>
 *
 * <p> Проверяет создание JPA-сущностей спутников различных типов через фабрики,
 * обработку неверных параметров и поддержку типов. </p>
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Тесты фабрик спутников")
class SatelliteFactoryTest {

    private static final String SATELLITE_NAME = "Тест-спутник";
    private static final double TEST_BATTERY = 0.8;
    private static final double TEST_BANDWIDTH = 500.0;
    private static final double TEST_RESOLUTION = 2.5;

    private final ISatelliteFactory commFactory = new CommunicationSatelliteFactory();
    private final ISatelliteFactory imgFactory = new ImagingSatelliteFactory();

    @Test
    @DisplayName("CommunicationSatelliteFactory должен создавать спутники связи из правильных параметров")
    void communicationFactory_WithValidParam_ShouldCreateCommunicationSatellite() {
        // Arrange
        SatelliteParam param = new CommunicationSatelliteParam(
                SATELLITE_NAME, TEST_BATTERY, TEST_BANDWIDTH
        );

        // Act
        SatelliteEntity satellite = commFactory.createSatelliteEntity(param);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof CommunicationSatelliteEntity);
        assertEquals(SATELLITE_NAME, satellite.getName());
        assertEquals(TEST_BATTERY, satellite.getEnergy().getBatteryLevel());
        assertEquals(TEST_BANDWIDTH, ((CommunicationSatelliteEntity) satellite).getBandwidth());
    }

    @Test
    @DisplayName("ImagingSatelliteFactory должен создавать спутники ДЗЗ из правильных параметров")
    void imagingFactory_WithValidParam_ShouldCreateImagingSatellite() {
        // Arrange
        SatelliteParam param = new ImagingSatelliteParam(
                SATELLITE_NAME, TEST_BATTERY, TEST_RESOLUTION
        );

        // Act
        SatelliteEntity satellite = imgFactory.createSatelliteEntity(param);

        // Assert
        assertNotNull(satellite);
        assertTrue(satellite instanceof ImagingSatelliteEntity);
        assertEquals(SATELLITE_NAME, satellite.getName());
        assertEquals(TEST_BATTERY, satellite.getEnergy().getBatteryLevel());
        assertEquals(TEST_RESOLUTION, ((ImagingSatelliteEntity) satellite).getResolution());
    }

    @Test
    @DisplayName("CommunicationSatelliteFactory должен выбрасывать исключение при передаче неверного типа параметров")
    void communicationFactory_WithWrongParamType_ShouldThrowException() {
        // Arrange
        SatelliteParam wrongParam = new ImagingSatelliteParam(
                SATELLITE_NAME, TEST_BATTERY, TEST_RESOLUTION
        );

        // Act & Assert
        SpaceOperationException exception = assertThrows(
                SpaceOperationException.class,
                () -> commFactory.createSatelliteEntity(wrongParam)
        );

        assertTrue(exception.getMessage().contains("ожидает параметры типа CommunicationSatelliteParam"));
    }

    @Test
    @DisplayName("ImagingSatelliteFactory должен выбрасывать исключение при передаче неверного типа параметров")
    void imagingFactory_WithWrongParamType_ShouldThrowException() {
        // Arrange
        SatelliteParam wrongParam = new CommunicationSatelliteParam(
                SATELLITE_NAME, TEST_BATTERY, TEST_BANDWIDTH
        );

        // Act & Assert
        SpaceOperationException exception = assertThrows(
                SpaceOperationException.class,
                () -> imgFactory.createSatelliteEntity(wrongParam)
        );

        assertTrue(exception.getMessage().contains("ожидает параметры типа ImagingSatelliteParam"));
    }

    @Test
    @DisplayName("isSatelliteTypeSupported должен правильно определять поддерживаемые типы")
    void isSatelliteTypeSupported_ShouldReturnCorrectValues() {
        // Assert
        assertTrue(commFactory.isSatelliteTypeSupported(SatelliteType.COMMUNICATION));
        assertFalse(commFactory.isSatelliteTypeSupported(SatelliteType.IMAGING));

        assertTrue(imgFactory.isSatelliteTypeSupported(SatelliteType.IMAGING));
        assertFalse(imgFactory.isSatelliteTypeSupported(SatelliteType.COMMUNICATION));
    }
}