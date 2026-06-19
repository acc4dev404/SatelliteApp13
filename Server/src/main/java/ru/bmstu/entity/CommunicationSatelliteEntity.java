package ru.bmstu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bmstu.constants.SatelliteConstants;

@Entity
@DiscriminatorValue("COMMUNICATION")
@Getter
@Setter
@NoArgsConstructor
public class CommunicationSatelliteEntity extends SatelliteEntity {

    @Column(nullable = false)
    private Double bandwidth;

    public CommunicationSatelliteEntity(String name, double batteryLevel, double bandwidth) {
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    @Override
    public void performMissionLogic() {
        if (getState().getIsActive()) {
            System.out.println(getName() + ": Передача данных со скоростью " + bandwidth + " Мбит/с");
            sendData(bandwidth);
            getEnergy().consume(SatelliteConstants.COMMUNICATION_ENERGY_CONSUMPTION);
        } else {
            System.out.println("\uD83D\uDED1 " + getName() + ": Не может выполнить миссию - не активен");
        }
    }

    private void sendData(double dataAmount) {
        System.out.println(getName() + ": Отправил " + dataAmount + " Мбит данных!");
    }
}