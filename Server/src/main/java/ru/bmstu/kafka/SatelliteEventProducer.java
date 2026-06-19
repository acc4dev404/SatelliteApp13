package ru.bmstu.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.bmstu.entity.CommunicationSatelliteEntity;
import ru.bmstu.entity.ImagingSatelliteEntity;
import ru.bmstu.entity.SatelliteEntity;

import java.time.Instant;

@Component
public class SatelliteEventProducer {

    private static final Logger log = LoggerFactory.getLogger(SatelliteEventProducer.class);
    private static final String TOPIC = "satellite-events";

    private final KafkaTemplate<String, SatelliteEvent> kafkaTemplate;

    public SatelliteEventProducer(KafkaTemplate<String, SatelliteEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSatelliteCreatedEvent(SatelliteEntity satellite) {
        SatelliteEvent event = buildEvent(satellite, SatelliteEvent.EventType.SATELLITE_CREATED);
        sendEvent(event);
    }

    public void sendSatelliteDeletedEvent(SatelliteEntity satellite) {
        SatelliteEvent event = buildEvent(satellite, SatelliteEvent.EventType.SATELLITE_DELETED);
        sendEvent(event);
    }

    private SatelliteEvent buildEvent(SatelliteEntity satellite, SatelliteEvent.EventType eventType) {
        SatelliteEvent.SatelliteEventBuilder builder = SatelliteEvent.builder()
                .eventType(eventType)
                .satelliteId(satellite.getId())
                .name(satellite.getName())
                .constellationName(satellite.getConstellation() != null ?
                        satellite.getConstellation().getName() : null)
                .batteryLevel(satellite.getEnergy().getBatteryLevel())
                .timestamp(Instant.now());

        if (satellite instanceof CommunicationSatelliteEntity comm) {
            builder.satelliteType("COMMUNICATION")
                    .bandwidth(comm.getBandwidth());
        } else if (satellite instanceof ImagingSatelliteEntity img) {
            builder.satelliteType("IMAGING")
                    .resolution(img.getResolution());
        }

        return builder.build();
    }

    private void sendEvent(SatelliteEvent event) {
        log.info("Отправка Kafka события: {} для спутника {}",
                event.getEventType(), event.getName());

        kafkaTemplate.send(TOPIC, event.getName(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Kafka событие отправлено: offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Ошибка отправки Kafka события: {}", ex.getMessage());
                    }
                });
    }
}