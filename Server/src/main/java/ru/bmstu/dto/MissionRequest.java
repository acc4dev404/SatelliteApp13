package ru.bmstu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * <p> DTO для запроса на выполнение миссий. </p>
 *
 * <p> Содержит список названий группировок, для которых нужно выполнить миссии,
 * а также флаги для активации спутников перед выполнением. </p>
 */
@Getter
@ToString
@AllArgsConstructor
public class MissionRequest {

    /** Список названий группировок для выполнения миссий. */
    private final List<String> constellationNames;

    /** Флаг необходимости активации спутников перед выполнением миссий. */
    private final boolean activateBeforeMission;

    /** Флаг необходимости отображения статуса после выполнения миссий. */
    private final boolean showStatusAfterMission;
}