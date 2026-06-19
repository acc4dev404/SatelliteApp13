package ru.bmstu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.bmstu.constants.EnergySystemConstants;
import ru.bmstu.model.satellite.EnergySystem;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Тесты для проверки работы Builder'а EnergySystem. </p>
 *
 * <p> Проверяет создание EnergySystem с использованием Lombok @Builder,
 * значения по умолчанию, валидацию граничных значений,
 * а также методы consume() и hasSufficientPower(). </p>
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Тесты Builder для EnergySystem")
class EnergySystemBuilderTest {

    private static final double TEST_BATTERY = 0.75;
    private static final double HIGH_BATTERY = 0.9;
    private static final double LOW_BATTERY = 0.1;
    private static final double CONSUME_AMOUNT = 0.1;

    @Test
    @DisplayName("Builder должен создавать EnergySystem с указанным уровнем заряда")
    void builder_ShouldCreateEnergySystemWithSpecifiedBatteryLevel() {
        // Act
        EnergySystem energySystem = EnergySystem.builder()
                .batteryLevel(TEST_BATTERY)
                .build();

        // Assert
        assertNotNull(energySystem);
        assertEquals(TEST_BATTERY, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("Builder должен использовать значение по умолчанию, если заряд не указан")
    void builder_ShouldUseDefaultBatteryLevel() {
        // Act
        EnergySystem energySystem = EnergySystem.builder().build();

        // Assert
        assertNotNull(energySystem);
        assertEquals(EnergySystemConstants.DEFAULT_BATTERY_LEVEL, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("Builder должен ограничивать максимальный заряд")
    void builder_ShouldClampMaxBatteryLevel() {
        // Act
        EnergySystem energySystem = EnergySystem.builder()
                .batteryLevel(1.5)
                .build();

        // Assert
        assertEquals(EnergySystemConstants.MAX_BATTERY, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("Builder должен ограничивать минимальный заряд")
    void builder_ShouldClampMinBatteryLevel() {
        // Act
        EnergySystem energySystem = EnergySystem.builder()
                .batteryLevel(-0.5)
                .build();

        // Assert
        assertEquals(EnergySystemConstants.MIN_BATTERY, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("EnergySystem должен правильно определять достаточность заряда")
    void hasSufficientPower_ShouldReturnCorrectValue() {
        // Act
        EnergySystem highBattery = EnergySystem.builder().batteryLevel(HIGH_BATTERY).build();
        EnergySystem lowBattery = EnergySystem.builder().batteryLevel(LOW_BATTERY).build();

        // Assert
        assertTrue(highBattery.hasSufficientPower());
        assertFalse(lowBattery.hasSufficientPower());
    }

    @Test
    @DisplayName("Consume должен уменьшать заряд")
    void consume_ShouldReduceBatteryLevel() {
        // Arrange
        EnergySystem energySystem = EnergySystem.builder().batteryLevel(TEST_BATTERY).build();

        // Act
        boolean result = energySystem.consume(CONSUME_AMOUNT);

        // Assert
        assertTrue(result);
        assertEquals(TEST_BATTERY - CONSUME_AMOUNT, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("Consume с отрицательным значением не должен изменять заряд")
    void consume_WithNegativeAmount_ShouldNotChangeBatteryLevel() {
        // Arrange
        EnergySystem energySystem = EnergySystem.builder().batteryLevel(TEST_BATTERY).build();

        // Act
        boolean result = energySystem.consume(-CONSUME_AMOUNT);

        // Assert
        assertFalse(result);
        assertEquals(TEST_BATTERY, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("Consume при нулевом заряде не должен изменять заряд")
    void consume_WithZeroBattery_ShouldNotChangeBatteryLevel() {
        // Arrange
        EnergySystem energySystem = EnergySystem.builder().batteryLevel(0.0).build();

        // Act
        boolean result = energySystem.consume(CONSUME_AMOUNT);

        // Assert
        assertFalse(result);
        assertEquals(0.0, energySystem.getBatteryLevel());
    }
}