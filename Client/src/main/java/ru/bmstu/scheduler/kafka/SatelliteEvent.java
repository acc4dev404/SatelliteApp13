package ru.bmstu.scheduler.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SatelliteEvent {

    public enum EventType {
        SATELLITE_CREATED,
        SATELLITE_DELETED
    }

    private EventType eventType;
    private Long satelliteId;
    private String name;
    private String constellationName;
    private String satelliteType;
    private Double batteryLevel;
    private Double bandwidth;
    private Double resolution;
    private Instant timestamp;
}