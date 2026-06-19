package ru.bmstu.telemetry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "inbox")
@Getter
@Setter
@NoArgsConstructor
public class InboxEntity {

    @Id
    @Column(name = "event_id", nullable = false, length = 255)
    private String eventId;

    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "processed_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant processedAt;

    public InboxEntity(String eventId, Long aggregateId, String eventType) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
    }
}