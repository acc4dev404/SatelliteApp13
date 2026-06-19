package ru.bmstu.constants;

/**
 * <p> Утилитарный класс, содержащий константы для системы управления энергией. </p>
 *
 * <p> Этот класс содержит все пороговые значения, граничные значения и значения по умолчанию,
 * используемые в {@link ru.bmstu.model.satellite.EnergySystem}. </p>
 */
public final class EnergySystemConstants {

    /** <p> Порог низкого заряда батареи (20%). Ниже этого значения спутник не может активироваться. </p> */
    public static final double LOW_BATTERY_THRESHOLD = 0.2;

    /** <p> Максимально допустимый уровень заряда батареи (100%). </p> */
    public static final double MAX_BATTERY = 1.0;

    /** <p> Минимально допустимый уровень заряда батареи (0%). </p> */
    public static final double MIN_BATTERY = 0.0;

    /** <p> Уровень заряда батареи по умолчанию (50%). Используется при создании EnergySystem без параметров. </p> */
    public static final double DEFAULT_BATTERY_LEVEL = 0.5;

    /**
     * <p> Приватный конструктор для предотвращения создания экземпляров утилитарного класса. </p>
     */
    private EnergySystemConstants() {

    }
}