package ru.bmstu.param;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * <p> Абстрактный базовый класс для параметров создания спутников.
 * Содержит общие для всех типов спутников поля: тип, имя и уровень заряда.
 * Конкретные классы-наследники добавляют специфические параметры
 * (пропускную способность, разрешение и т.д.). </p>
 *
 * <p> Реализует паттерн Command - объект параметра инкапсулирует всю информацию,
 * необходимую для создания спутника определенного типа. </p>
 */
@Getter
@ToString
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CommunicationSatelliteParam.class, name = "COMMUNICATION"),
        @JsonSubTypes.Type(value = ImagingSatelliteParam.class, name = "IMAGING")
})
public abstract class SatelliteParam {

    /** <p> Тип спутника, который нужно создать. </p> */
    private final SatelliteType type;

    /** <p> Имя спутника. </p> */
    private final String name;

    /** <p> Начальный уровень заряда батареи (от 0.0 до 1.0). </p> */
    private final double batteryLevel;
}