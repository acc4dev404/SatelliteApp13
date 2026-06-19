package ru.bmstu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "satellites")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "satellite_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class SatelliteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constellation_id")
    private ConstellationEntity constellation;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "energy_system_id")
    private EnergySystemEntity energySystem;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "satellite_state_id")
    private SatelliteStateEntity state;

    @Column(name = "internal_temperature")
    private Double internalTemperature;

    @Column(name = "external_temperature")
    private Double externalTemperature;

    @Column(name = "battery_voltage")
    private Double batteryVoltage;

    @Column(name = "last_telemetry_time")
    private Instant lastTelemetryTime;

    public SatelliteEntity(String name, double batteryLevel) {
        this.name = name;
        this.energySystem = new EnergySystemEntity(batteryLevel);
        this.state = new SatelliteStateEntity();
    }

    /**
     * <p> Возвращает систему управления энергией. </p>
     *
     * @return EnergySystemEntity
     */
    public EnergySystemEntity getEnergy() {
        return energySystem;
    }

    /**
     * <p> Возвращает состояние спутника. </p>
     *
     * @return SatelliteStateEntity
     */
    public SatelliteStateEntity getState() {
        return state;
    }

    /**
     * <p> Возвращает имя спутника. </p>
     *
     * @return имя спутника
     */
    public String getName() {
        return name;
    }

    /**
     * <p> Выполняет миссию спутника. </p>
     */
    public abstract void performMissionLogic();

    public Double getInternalTemperature() {
        return internalTemperature;
    }

    public void setInternalTemperature(Double internalTemperature) {
        this.internalTemperature = internalTemperature;
    }

    public Double getExternalTemperature() {
        return externalTemperature;
    }

    public void setExternalTemperature(Double externalTemperature) {
        this.externalTemperature = externalTemperature;
    }

    public Double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public Instant getLastTelemetryTime() {
        return lastTelemetryTime;
    }

    public void setLastTelemetryTime(Instant lastTelemetryTime) {
        this.lastTelemetryTime = lastTelemetryTime;
    }
}