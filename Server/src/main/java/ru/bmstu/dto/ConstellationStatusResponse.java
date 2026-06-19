package ru.bmstu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * <p> DTO для ответа со статусом группировки. </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConstellationStatusResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Название группировки. */
    private String constellationName;

    /** Количество спутников в группировке. */
    private int satelliteCount;

    /** Карта статусов спутников (имя -> состояние). */
    private Map<String, String> satelliteStatuses;

    /** Карта уровней заряда спутников (имя -> уровень). */
    private Map<String, Double> batteryLevels;
}