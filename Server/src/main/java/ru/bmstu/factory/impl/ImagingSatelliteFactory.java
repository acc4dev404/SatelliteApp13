package ru.bmstu.factory.impl;

import org.springframework.stereotype.Component;
import ru.bmstu.exception.SpaceOperationException;
import ru.bmstu.factory.ISatelliteFactory;
import ru.bmstu.entity.ImagingSatelliteEntity;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.param.ImagingSatelliteParam;
import ru.bmstu.param.SatelliteParam;
import ru.bmstu.param.SatelliteType;

/**
 * <p> Конкретная фабрика для создания спутников ДЗЗ {@link ImagingSatelliteEntity}. </p>
 *
 * <p> Поддерживает только тип {@link SatelliteType#IMAGING}. </p>
 * <p> Ожидает параметры типа {@link ImagingSatelliteParam}. </p>
 *
 * <p> Фабрика не хранит параметры - все необходимые значения передаются
 * через объект {@link ImagingSatelliteParam} при вызове
 * {@link #createSatelliteEntity(SatelliteParam)}. </p>
 */
@Component
public class ImagingSatelliteFactory implements ISatelliteFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public SatelliteEntity createSatelliteEntity(SatelliteParam param) {

        if (!(param instanceof ImagingSatelliteParam)) {
            throw new SpaceOperationException(
                    "ImagingSatelliteFactory ожидает параметры типа ImagingSatelliteParam, получен: "
                            + param.getClass().getSimpleName()
            );
        }

        ImagingSatelliteParam imgParam = (ImagingSatelliteParam) param;

        return new ImagingSatelliteEntity(
                imgParam.getName(),
                imgParam.getBatteryLevel(),
                imgParam.getResolution()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type) {
        return SatelliteType.IMAGING == type;
    }
}