package ru.bmstu.param;

import lombok.Getter;
import lombok.ToString;

/**
 * <p> Параметры для создания спутника связи {@link ru.bmstu.model.satellite.CommunicationSatellite}. </p>
 *
 * <p> Добавляет к базовым параметрам пропускную способность (bandwidth). </p>
 */
@Getter
@ToString(callSuper = true)
public class CommunicationSatelliteParam extends SatelliteParam {

    /** <p> Пропускная способность спутника (Мбит/с). </p> */
    private final double bandwidth;

    /**
     * <p> Создает новый объект параметров для спутника связи. </p>
     *
     * @param name имя спутника
     * @param batteryLevel уровень заряда
     * @param bandwidth пропускная способность
     */
    public CommunicationSatelliteParam(String name, double batteryLevel, double bandwidth) {
        super(SatelliteType.COMMUNICATION, name, batteryLevel);
        this.bandwidth = bandwidth;
    }
}