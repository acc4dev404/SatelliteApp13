package ru.bmstu.constants;

/**
 * <p> Утилитарный класс, содержащий константы для различных типов спутников. </p>
 *
 * <p> Этот класс содержит параметры конфигурации для спутников связи и ДЗЗ,
 * включая энергопотребление, значения по умолчанию и граничные значения. </p>
 */
public final class SatelliteConstants {

    /** <p> Энергопотребление спутника связи за одну миссию (5%). </p> */
    public static final double COMMUNICATION_ENERGY_CONSUMPTION = 0.05;

    /** <p> Энергопотребление спутника ДЗЗ за одну миссию (8%). </p> */
    public static final double IMAGING_ENERGY_CONSUMPTION = 0.08;

    /** <p> Пропускная способность спутника связи по умолчанию (100 Мбит/с). </p> */
    public static final double DEFAULT_COMMUNICATION_BANDWIDTH = 100.0;

    /** <p> Разрешение спутника ДЗЗ по умолчанию (1 метр на пиксель). </p> */
    public static final double DEFAULT_IMAGING_RESOLUTION = 1.0;

    /** <p> Минимально допустимая пропускная способность (10 Мбит/с). </p> */
    public static final double MIN_BANDWIDTH = 10.0;

    /** <p> Максимально допустимая пропускная способность (10 Гбит/с). </p> */
    public static final double MAX_BANDWIDTH = 10000.0;

    /** <p> Минимально допустимое разрешение (0.1 метра на пиксель). </p> */
    public static final double MIN_RESOLUTION = 0.1;

    /** <p> Максимально допустимое разрешение (100 метров на пиксель). </p> */
    public static final double MAX_RESOLUTION = 100.0;

    /**
     * <p> Приватный конструктор для предотвращения создания экземпляров утилитарного класса. </p>
     */
    private SatelliteConstants() {
    }
}