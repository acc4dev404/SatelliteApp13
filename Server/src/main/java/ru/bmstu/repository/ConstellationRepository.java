package ru.bmstu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.entity.ConstellationEntity;

import java.util.Optional;

/**
 * <p> JPA-репозиторий для работы с группировками спутников. </p>
 *
 * <p> Предоставляет методы для CRUD операций с группировками
 * и поиска по имени. </p>
 */
@Repository
public interface ConstellationRepository extends JpaRepository<ConstellationEntity, Long> {

    /**
     * <p> Находит группировку по её имени. </p>
     *
     * @param name название группировки
     * @return Optional с найденной группировкой или пустой Optional
     */
    Optional<ConstellationEntity> findByName(String name);

    /**
     * <p> Проверяет, существует ли группировка с указанным именем. </p>
     *
     * @param name название группировки
     * @return true если группировка существует, false в противном случае
     */
    boolean existsByName(String name);
}