package ru.bmstu.model.satellite;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.bmstu.constants.EnergySystemConstants;

/**
 * <p> Класс, представляющий систему управления энергией спутника. </p>
 *
 * <p> Отвечает за хранение уровня заряда батареи, его расходование и проверку
 * достаточности энергии для активации. Использует Lombok {@link Builder} для
 *  * гибкого создания экземпляров с валидацией параметров.
 *
 * <p>Пример использования Builder:</p>
 * <pre>{@code
 * EnergySystem energy = EnergySystem.builder()
 *         .batteryLevel(0.85)
 *         .build();
 * }</pre>
 */
@Getter
@ToString
@Builder
public class EnergySystem {

    /** Текущий уровень заряда батареи (от 0.0 до 1.0). */
    private double batteryLevel;

    /**
     * <p> Внутренний класс Builder, генерируемый Lombok. </p>
     * <p> Расширяет стандартный Lombok Builder добавлением валидации и значения по умолчанию. </p>
     */
    public static class EnergySystemBuilder {

        /** <p> Уровень заряда с значением по умолчанию. </p> */
        private double batteryLevel = EnergySystemConstants.DEFAULT_BATTERY_LEVEL;

        /**
         * <p> Устанавливает уровень заряда батареи с автоматической валидацией. </p>
         * <p> Значение автоматически ограничивается диапазоном [MIN_BATTERY, MAX_BATTERY]. </p>
         *
         * @param batteryLevel желаемый уровень заряда
         * @return этот Builder для цепочечных вызовов
         */
        public EnergySystemBuilder batteryLevel(double batteryLevel) {
            this.batteryLevel = Math.max(EnergySystemConstants.MIN_BATTERY,
                    Math.min(EnergySystemConstants.MAX_BATTERY, batteryLevel));
            return this;
        }
    }

    /**
     * <p> Расходует указанное количество энергии. </p>
     * <p> Если количество для расхода отрицательное или текущий заряд уже минимальный,
     * операция не выполняется и возвращается false. </p>
     *
     * @param amount количество энергии для расхода (должно быть положительным)
     * @return true если энергия успешно израсходована, false в противном случае
     */
    public boolean consume(double amount) {
        if (amount <= 0 || batteryLevel <= EnergySystemConstants.MIN_BATTERY) {
            return false;
        }
        batteryLevel = Math.max(EnergySystemConstants.MIN_BATTERY, batteryLevel - amount);
        return true;
    }

    /**
     * <p> Проверяет, достаточно ли энергии для активации спутника. </p>
     *
     * @return true если уровень заряда выше порога LOW_BATTERY_THRESHOLD
     */
    public boolean hasSufficientPower() {
        return batteryLevel > EnergySystemConstants.LOW_BATTERY_THRESHOLD;
    }
}