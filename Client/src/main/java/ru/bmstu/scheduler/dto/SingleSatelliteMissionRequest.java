package ru.bmstu.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO для запроса на выполнение миссии над конкретным спутником.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SingleSatelliteMissionRequest {

    /** Название группировки. */
    private String constellationName;

    /** Имя спутника. */
    private String satelliteName;

    /** Флаг активации перед миссией. */
    private boolean activateBeforeMission;
}