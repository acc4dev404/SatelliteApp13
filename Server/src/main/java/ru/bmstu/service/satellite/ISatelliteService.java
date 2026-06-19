package ru.bmstu.service.satellite;

import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.param.SatelliteParam;

import java.util.List;
import java.util.Optional;

/**
 * <p> Сервис для создания спутников различных типов. </p>
 *
 * <p> Реализует паттерн "Стратегия" (Strategy) - выбор подходящей фабрики
 * делегируется алгоритму, основанному на типе параметра. </p>
 */
public interface ISatelliteService {

    /**
     * <p> Создает спутник на основе переданных параметров. </p>
     *
     * <p> Автоматически выбирает подходящую фабрику по типу спутника
     * и делегирует создание ей. </p>
     *
     * @param param параметры для создания спутника
     * @return созданная JPA-сущность спутника
     */
    SatelliteEntity createSatellite(SatelliteParam param);

    Optional<SatelliteEntity> getSatelliteById(Long id);

    List<SatelliteEntity> getAllSatellites();

    Optional<SatelliteEntity> findByName(String constellationName, String satelliteName);

    void updateSatellite(Long id, SatelliteParam param);

    void deleteSatellite(Long id);
}