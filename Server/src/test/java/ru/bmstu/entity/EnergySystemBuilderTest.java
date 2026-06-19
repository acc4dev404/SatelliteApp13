package ru.bmstu.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.bmstu.constants.EnergySystemConstants;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Тесты для проверки работы EnergySystemEntity. </p>
 *
 * <p> Проверяет создание EnergySystemEntity, значения по умолчанию,
 * валидацию граничных значений, а также методы consume() и hasSufficientPower(). </p>
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Тесты EnergySystemEntity")
class EnergySystemBuilderTest {

    private static final double TEST_BATTERY = 0.75;
    private static final double HIGH_BATTERY = 0.9;
    private static final double LOW_BATTERY = 0.1;
    private static final double CONSUME_AMOUNT = 0.1;

    @Test
    @DisplayName("Конструктор должен создавать EnergySystemEntity с указанным уровнем заряда")
    void constructor_ShouldCreateEnergySystemWithSpecifiedBatteryLevel() {
        // Act
        EnergySystemEntity energySystem = new EnergySystemEntity(TEST_BATTERY);

        // Assert
        assertNotNull(energySystem);
        assertEquals(TEST_BATTERY, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("Конструктор должен использовать значение по умолчанию, если заряд null")
    void constructor_ShouldUseDefaultBatteryLevel() {
        // Act
        EnergySystemEntity energySystem = new EnergySystemEntity(null);

        // Assert
        assertNotNull(energySystem);
        assertEquals(EnergySystemConstants.DEFAULT_BATTERY_LEVEL, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("Конструктор должен ограничивать максимальный заряд")
    void constructor_ShouldClampMaxBatteryLevel() {
        // Act
        EnergySystemEntity energySystem = new EnergySystemEntity(1.5);

        // Assert
        assertEquals(EnergySystemConstants.MAX_BATTERY, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("Конструктор должен ограничивать минимальный заряд")
    void constructor_ShouldClampMinBatteryLevel() {
        // Act
        EnergySystemEntity energySystem = new EnergySystemEntity(-0.5);

        // Assert
        assertEquals(EnergySystemConstants.MIN_BATTERY, energySystem.getBatteryLevel());
    }

    @Test
    @DisplayName("EnergySystemEntity должен правильно определять достаточность заряда")
    void hasSufficientPower_ShouldReturnCorrectValue() {
        // Act
        EnergySystemEntity highBattery = new EnergySystemEntity(HIGH_BATTERY);
        EnergySystemEntity lowBattery = new EnergySystemEntity(LOW_BATTERY);

        // Assert
        assertTrue(highBattery.hasSufficientPower());
        assertFalse(lowBattery.hasSufficientPower());
    }

    @Test
    @DisplayName("Consume должен уменьшать заряд")
    void consume_ShouldReduceBatteryLevel() {
        // Arrange
        EnergySystemEntity energySystem = new EnergySystemEntity(TEST_BATTERY);

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
        EnergySystemEntity energySystem = new EnergySystemEntity(TEST_BATTERY);

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
        EnergySystemEntity energySystem = new EnergySystemEntity(0.0);

        // Act
        boolean result = energySystem.consume(CONSUME_AMOUNT);

        // Assert
        assertFalse(result);
        assertEquals(0.0, energySystem.getBatteryLevel());
    }
}