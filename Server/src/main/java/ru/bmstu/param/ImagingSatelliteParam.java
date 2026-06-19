package ru.bmstu.param;

import lombok.Getter;
import lombok.ToString;

/**
 * <p> Параметры для создания спутника ДЗЗ {@link ru.bmstu.model.satellite.ImagingSatellite}. </p>
 *
 * <p> Добавляет к базовым параметрам разрешение съемки (resolution). </p>
 */
@Getter
@ToString(callSuper = true)
public class ImagingSatelliteParam extends SatelliteParam {

    /** <p> Разрешение съемки (метров на пиксель). </p> */
    private final double resolution;

    /**
     * <p> Создает новый объект параметров для спутника ДЗЗ. </p>
     *
     * @param name имя спутника
     * @param batteryLevel уровень заряда
     * @param resolution разрешение съемки
     */
    public ImagingSatelliteParam(String name, double batteryLevel, double resolution) {
        super(SatelliteType.IMAGING, name, batteryLevel);
        this.resolution = resolution;
    }
}