package ru.bmstu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bmstu.entity.SatelliteEntity;

import java.util.List;
import java.util.Optional;

/**
 * <p> JPA-репозиторий для работы со спутниками. </p>
 *
 * <p> Предоставляет методы для CRUD операций со спутниками
 * и поиска по различным критериям. </p>
 */
@Repository
public interface SatelliteRepository extends JpaRepository<SatelliteEntity, Long> {

    /**
     * <p> Находит спутник по имени и названию группировки. </p>
     *
     * @param name имя спутника
     * @param constellationName название группировки
     * @return Optional с найденным спутником или пустой Optional
     */
    Optional<SatelliteEntity> findByNameAndConstellationName(String name, String constellationName);

    /**
     * <p> Находит все спутники в указанной группировке. </p>
     *
     * @param constellationName название группировки
     * @return список спутников в группировке
     */
    List<SatelliteEntity> findByConstellationName(String constellationName);

    /**
     * <p> Находит спутник по названию группировки и имени спутника. </p>
     *
     * @param constellationName название группировки
     * @param satelliteName имя спутника
     * @return Optional с найденным спутником или пустой Optional
     */
    @Query("SELECT s FROM SatelliteEntity s WHERE s.constellation.name = :constellationName AND s.name = :satelliteName")
    Optional<SatelliteEntity> findSatellite(@Param("constellationName") String constellationName,
                                            @Param("satelliteName") String satelliteName);

    /**
     * <p> Находит спутник по имени. </p>
     *
     * @param name имя спутника
     * @return Optional с найденным спутником или пустой Optional
     */
    Optional<SatelliteEntity> findByName(String name);

    /**
     * <p> Находит список спутников по имени (возвращает все совпадения). </p>
     *
     * @param name имя спутника
     * @return список спутников с указанным именем
     */
    List<SatelliteEntity> findAllByName(String name);

    /**
     * Находит спутник по имени с предварительной загрузкой всех связей.
     * Решает проблему LazyInitializationException.
     */
    @Query("SELECT s FROM SatelliteEntity s " +
            "LEFT JOIN FETCH s.energySystem " +
            "LEFT JOIN FETCH s.state " +
            "WHERE s.name = :name")
    Optional<SatelliteEntity> findByNameWithDetails(@Param("name") String name);

    /**
     * Находит первый спутник с указанным именем.
     * Используется когда могут быть дубликаты.
     */
    default Optional<SatelliteEntity> findFirstByName(String name) {
        List<SatelliteEntity> satellites = findAllByName(name);
        return satellites.isEmpty() ? Optional.empty() : Optional.of(satellites.get(0));
    }
}