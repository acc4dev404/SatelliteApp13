package ru.bmstu.telemetry.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.telemetry.entity.InboxEntity;
import ru.bmstu.telemetry.kafka.SatelliteEvent;
import ru.bmstu.telemetry.repository.InboxRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SatelliteCacheService {

    private static final Logger log = LoggerFactory.getLogger(SatelliteCacheService.class);

    private final Map<String, SatelliteEvent> satellites = new ConcurrentHashMap<>();
    private final InboxRepository inboxRepository;

    public SatelliteCacheService(InboxRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    public Map<String, SatelliteEvent> getAllSatellites() {
        return satellites;
    }

    /**
     * Обработка события из Kafka с идемпотентностью через Inbox.
     * Генерируем уникальный ID события на основе satelliteId + eventType + timestamp.
     */
    @KafkaListener(topics = "satellite-events", groupId = "${spring.application.name}")
    @Transactional
    public void handleSatelliteEvent(SatelliteEvent event) {

        String eventId = generateEventId(event);

        log.info("📡 TelemetryService получил Kafka событие: {} для спутника {} (id={})",
                event.getEventType(), event.getName(), eventId);

        if (inboxRepository.existsByEventId(eventId)) {
            log.warn("⚠️ Событие {} уже обработано, пропускаем", eventId);
            return;
        }

        InboxEntity inbox = new InboxEntity(eventId, event.getSatelliteId(), event.getEventType().name());
        inboxRepository.save(inbox);

        if (event.getEventType() == SatelliteEvent.EventType.SATELLITE_CREATED) {
            satellites.put(event.getName(), event);
            log.info("✅ Спутник {} добавлен в кэш телеметрии. Всего спутников: {}",
                    event.getName(), satellites.size());
        } else if (event.getEventType() == SatelliteEvent.EventType.SATELLITE_DELETED) {
            satellites.remove(event.getName());
            log.info("❌ Спутник {} удалён из кэша телеметрии. Всего спутников: {}",
                    event.getName(), satellites.size());
        }
    }

    /**
     * Генерирует уникальный ID события на основе данных события.
     * Формат: satelliteId_eventType_timestamp
     */
    private String generateEventId(SatelliteEvent event) {
        return event.getSatelliteId() + "_" + event.getEventType().name() + "_" + event.getTimestamp().toEpochMilli();
    }

    public boolean hasSatellites() {
        return !satellites.isEmpty();
    }
}