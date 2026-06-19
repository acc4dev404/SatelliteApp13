package ru.bmstu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bmstu.entity.OutboxEntity;
import ru.bmstu.entity.OutboxStatus;

import java.util.List;

/**
 * Репозиторий для работы с outbox таблицей.
 * Поддерживает пагинацию и работу со статусами через Enum.
 */
@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {

    /**
     * Находит страницу неотправленных событий (статус PENDING).
     * Используется планировщиком для пакетной обработки.
     *
     * @param status   статус события (PENDING, SENT, FAILED)
     * @param pageable параметры пагинации (номер страницы, размер, сортировка)
     * @return страница с outbox событиями
     */
    @Query("SELECT o FROM OutboxEntity o WHERE o.status = :status ORDER BY o.createdAt ASC")
    Page<OutboxEntity> findByStatus(@Param("status") OutboxStatus status, Pageable pageable);

    /**
     * Обновляет статус события по ID.
     *
     * @param id     идентификатор события
     * @param status новый статус
     * @return количество обновлённых записей
     */
    @Modifying
    @Query("UPDATE OutboxEntity o SET o.status = :status WHERE o.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") OutboxStatus status);

    /**
     * Находит все события с определённым статусом (без пагинации).
     * Используется для мониторинга или ручных операций.
     *
     * @param status статус события
     * @return список outbox событий
     */
    List<OutboxEntity> findAllByStatus(OutboxStatus status);

    /**
     * Подсчитывает количество событий с определённым статусом.
     *
     * @param status статус события
     * @return количество событий
     */
    long countByStatus(OutboxStatus status);

    /**
     * Удаляет отправленные события старше указанной даты.
     * Используется для очистки таблицы от старых записей.
     *
     * @param status      статус события (обычно SENT)
     * @param olderThan   дата, ранее которой нужно удалить
     * @return количество удалённых записей
     */
    @Modifying
    @Query("DELETE FROM OutboxEntity o WHERE o.status = :status AND o.createdAt < :olderThan")
    int deleteOldSentEvents(@Param("status") OutboxStatus status,
                            @Param("olderThan") java.time.Instant olderThan);
}