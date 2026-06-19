package ru.bmstu.model.satellite;

import lombok.Getter;
import lombok.ToString;

/**
 * <p> Класс, представляющий состояние спутника. </p>
 *
 * <p> Отвечает только за управление состоянием спутника (активен/не активен)
 * и соответствующие статусные сообщения. Реализует принцип единственной
 * ответственности (SRP). </p>
 */
@Getter
@ToString
public class SatelliteState {

    /** <p> Флаг активности спутника. </p> */
    private boolean isActive = false;

    /** <p> Текстовое описание текущего состояния. </p> */
    private String statusMessage;

    /**
     * <p> Создает новое состояние спутника. </p>
     * <p> По умолчанию спутник не активен со статусом "Не активирован". </p>
     */
    public SatelliteState() {
        this.statusMessage = "Не активирован";
    }

    /**
     * <p> Активирует спутник, если достаточно энергии и он еще не активен. </p>
     *
     * @param hasSufficientPower флаг достаточности энергии
     * @return true если активация успешна, false в противном случае
     */
    public boolean activate(boolean hasSufficientPower) {
        if (hasSufficientPower && !isActive) {
            isActive = true;
            statusMessage = "Активен";
            return true;
        }
        statusMessage = hasSufficientPower ? "Уже активен" : "Недостаточно энергии";
        return false;
    }

    /**
     * <p> Деактивирует спутник. </p>
     */
    public void deactivate() {
        isActive = false;
        statusMessage = "Деактивирован";
    }
}