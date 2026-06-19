package ru.bmstu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import ru.bmstu.param.SatelliteParam;

import java.util.List;

/**
 * <p> DTO для запроса на создание группировки со спутниками. </p>
 *
 * <p> Содержит название группировки и список параметров спутников для создания. </p>
 */
@Getter
@ToString
@AllArgsConstructor
public class CreateConstellationRequest {

    /** Название создаваемой группировки. */
    private final String constellationName;

    /** Список параметров спутников для добавления в группировку. */
    private final List<SatelliteParam> satelliteParams;
}