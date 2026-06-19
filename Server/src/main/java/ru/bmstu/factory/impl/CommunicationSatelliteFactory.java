package ru.bmstu.factory.impl;

import org.springframework.stereotype.Component;
import ru.bmstu.exception.SpaceOperationException;
import ru.bmstu.factory.ISatelliteFactory;
import ru.bmstu.entity.CommunicationSatelliteEntity;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.param.CommunicationSatelliteParam;
import ru.bmstu.param.SatelliteParam;
import ru.bmstu.param.SatelliteType;

/**
 * <p> Конкретная фабрика для создания спутников связи {@link CommunicationSatelliteEntity}. </p>
 *
 * <p> Поддерживает только тип {@link SatelliteType#COMMUNICATION}. </p>
 *
 * <p> Ожидает параметры типа {@link CommunicationSatelliteParam}. </p>
 *
 * <p> Фабрика не хранит параметры - все необходимые значения передаются
 * через объект {@link CommunicationSatelliteParam} при вызове
 * {@link #createSatelliteEntity(SatelliteParam)}. </p>
 */
@Component
public class CommunicationSatelliteFactory implements ISatelliteFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public SatelliteEntity createSatelliteEntity(SatelliteParam param) {
        if (!(param instanceof CommunicationSatelliteParam)) {
            throw new SpaceOperationException(
                    "CommunicationSatelliteFactory ожидает параметры типа CommunicationSatelliteParam, получен: "
                            + param.getClass().getSimpleName()
            );
        }

        CommunicationSatelliteParam commParam = (CommunicationSatelliteParam) param;

        return new CommunicationSatelliteEntity(
                commParam.getName(),
                commParam.getBatteryLevel(),
                commParam.getBandwidth()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type) {
        return SatelliteType.COMMUNICATION == type;
    }
}