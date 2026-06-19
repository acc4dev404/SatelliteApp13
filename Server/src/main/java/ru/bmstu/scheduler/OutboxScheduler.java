package ru.bmstu.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.entity.OutboxEntity;
import ru.bmstu.entity.OutboxStatus;
import ru.bmstu.kafka.SatelliteEvent;
import ru.bmstu.repository.OutboxRepository;

@Component
public class OutboxScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxScheduler.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, SatelliteEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${outbox.batch.size:100}")
    private int batchSize;

    public OutboxScheduler(OutboxRepository outboxRepository,
                           KafkaTemplate<String, SatelliteEvent> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${outbox.scheduler.fixedDelay:5000}",
            initialDelayString = "${outbox.scheduler.initialDelay:5000}")
    @Transactional
    public void processOutboxEvents() {
        Pageable pageable = PageRequest.of(0, batchSize);
        var pendingEvents = outboxRepository.findByStatus(OutboxStatus.PENDING, pageable);

        if (!pendingEvents.hasContent()) {
            return;
        }

        log.info("📤 Outbox планировщик: страница {}/{} ({} событий)",
                pendingEvents.getNumber() + 1,
                pendingEvents.getTotalPages(),
                pendingEvents.getNumberOfElements());

        for (OutboxEntity event : pendingEvents) {
            processEvent(event);
        }
    }

    private void processEvent(OutboxEntity event) {
        try {
            SatelliteEvent satelliteEvent = objectMapper.readValue(event.getPayload(), SatelliteEvent.class);

            kafkaTemplate.send("satellite-events", satelliteEvent);

            outboxRepository.updateStatus(event.getId(), OutboxStatus.SENT);
            log.debug("✅ Outbox событие {} (спутник {}) отправлено",
                    event.getId(), satelliteEvent.getSatelliteId());

        } catch (Exception e) {
            log.error("❌ Ошибка отправки outbox события {}: {}", event.getId(), e.getMessage());
            outboxRepository.updateStatus(event.getId(), OutboxStatus.FAILED);
        }
    }
}