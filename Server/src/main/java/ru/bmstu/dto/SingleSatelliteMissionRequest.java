package ru.bmstu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SingleSatelliteMissionRequest {
    private final String constellationName;
    private final String satelliteName;
    private final boolean activateBeforeMission;
}