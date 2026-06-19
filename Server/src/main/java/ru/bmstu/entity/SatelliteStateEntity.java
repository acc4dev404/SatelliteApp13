package ru.bmstu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "satellite_states")
@Getter
@Setter
@NoArgsConstructor
public class SatelliteStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "status_message")
    private String statusMessage = "Не активирован";

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

    /**
     * <p> Возвращает статус активности. </p>
     *
     * @return true если активен, false если не активен
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * <p> Возвращает текстовое сообщение о статусе. </p>
     *
     * @return статусное сообщение
     */
    public String getStatusMessage() {
        return statusMessage;
    }
}