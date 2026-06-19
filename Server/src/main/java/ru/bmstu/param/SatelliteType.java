package ru.bmstu.param;

import ru.bmstu.service.satellite.ISatelliteService;

/**
 * <p> Перечисление возможных типов спутников в системе. </p>
 *
 * <p> Используется для идентификации типа спутника при создании через фабрики
 * и для выбора подходящей фабрики в {@link ISatelliteService}. </p>
 */
public enum SatelliteType {

    /** <p> Спутник дистанционного зондирования Земли (делает снимки). </p> */
    IMAGING,

    /** <p> Спутник связи (передает данные). </p> */
    COMMUNICATION
}