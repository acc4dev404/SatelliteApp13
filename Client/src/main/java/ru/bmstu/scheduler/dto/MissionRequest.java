package ru.bmstu.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * DTO для запроса на выполнение миссий.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MissionRequest {

    /** Список названий группировок для выполнения миссий. */
    private List<String> constellationNames;

    /** Флаг необходимости активации спутников перед выполнением миссий. */
    private boolean activateBeforeMission;

    /** Флаг необходимости отображения статуса после выполнения миссий. */
    private boolean showStatusAfterMission;
}