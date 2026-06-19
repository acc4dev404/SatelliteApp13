package ru.bmstu.scheduler.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SatelliteEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(SatelliteEventConsumer.class);

    private final Map<String, SatelliteEvent> satellites = new ConcurrentHashMap<>();

    @KafkaListener(topics = "satellite-events", groupId = "${spring.application.name}")
    public void handleSatelliteEvent(SatelliteEvent event) {
        log.info("📡 Планировщик получил Kafka событие: {} для спутника {}",
                event.getEventType(), event.getName());

        if (event.getEventType() == SatelliteEvent.EventType.SATELLITE_CREATED) {
            satellites.put(event.getName(), event);
            log.info("Спутник {} добавлен в кэш планировщика. Всего спутников: {}",
                    event.getName(), satellites.size());
        } else if (event.getEventType() == SatelliteEvent.EventType.SATELLITE_DELETED) {
            satellites.remove(event.getName());
            log.info("Спутник {} удалён из кэша планировщика. Всего спутников: {}",
                    event.getName(), satellites.size());
        }
    }

    public boolean isSatelliteExists(String constellationName, String satelliteName) {
        return satellites.containsKey(satelliteName);
    }

    public Map<String, SatelliteEvent> getAllSatellites() {
        return satellites;
    }
}