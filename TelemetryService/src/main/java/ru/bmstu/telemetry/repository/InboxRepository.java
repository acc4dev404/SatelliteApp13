package ru.bmstu.telemetry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.telemetry.entity.InboxEntity;

@Repository
public interface InboxRepository extends JpaRepository<InboxEntity, String> {

    boolean existsByEventId(String eventId);
}