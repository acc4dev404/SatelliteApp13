package ru.bmstu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "energy_systems")
@Getter
@Setter
@NoArgsConstructor
public class EnergySystemEntity {

    /** <p> Порог низкого заряда батареи (20%). </p> */
    public static final double LOW_BATTERY_THRESHOLD = 0.2;

    /** <p> Максимально допустимый уровень заряда батареи (100%). </p> */
    public static final double MAX_BATTERY = 1.0;

    /** <p> Минимально допустимый уровень заряда батареи (0%). </p> */
    public static final double MIN_BATTERY = 0.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "battery_level", nullable = false)
    private Double batteryLevel;

    public EnergySystemEntity(Double batteryLevel) {
        this.batteryLevel = Math.max(MIN_BATTERY, Math.min(MAX_BATTERY, batteryLevel != null ? batteryLevel : 0.5));
    }

    /**
     * <p> Расходует указанное количество энергии. </p>
     *
     * @param amount количество энергии для расхода
     * @return true если энергия успешно израсходована, false в противном случае
     */
    public boolean consume(double amount) {
        if (amount <= 0 || batteryLevel <= MIN_BATTERY) {
            return false;
        }
        batteryLevel = Math.max(MIN_BATTERY, batteryLevel - amount);
        return true;
    }

    /**
     * <p> Проверяет, достаточно ли энергии для активации спутника. </p>
     *
     * @return true если уровень заряда выше порога LOW_BATTERY_THRESHOLD
     */
    public boolean hasSufficientPower() {
        return batteryLevel > LOW_BATTERY_THRESHOLD;
    }

    /**
     * <p> Возвращает уровень заряда батареи. </p>
     *
     * @return уровень заряда (от 0.0 до 1.0)
     */
    public Double getBatteryLevel() {
        return batteryLevel;
    }
}