package ru.bmstu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ru.bmstu.param.SatelliteParam;

/**
 * <p> DTO для запроса на добавление спутника в группировку. </p>
 *
 * <p> Содержит название группировки и параметры спутника для создания. </p>
 */
@Getter
@ToString
@AllArgsConstructor
public class AddSatelliteRequest {

    /** Название группировки, в которую добавляется спутник. */
    private final String constellationName;

    /** Параметры спутника для создания. */
    private final SatelliteParam satelliteParam;
}