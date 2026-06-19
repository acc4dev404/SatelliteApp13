package ru.bmstu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bmstu.constants.SatelliteConstants;

@Entity
@DiscriminatorValue("IMAGING")
@Getter
@Setter
@NoArgsConstructor
public class ImagingSatelliteEntity extends SatelliteEntity {

    @Column(nullable = false)
    private Double resolution;

    @Column(name = "photos_taken")
    private Integer photosTaken = 0;

    public ImagingSatelliteEntity(String name, double batteryLevel, double resolution) {
        super(name, batteryLevel);
        this.resolution = resolution;
        this.photosTaken = 0;
    }

    @Override
    public void performMissionLogic() {
        if (getState().getIsActive()) {
            System.out.println(getName() + ": Съемка территории с разрешением " + resolution + " м/пиксель");
            takePhoto();
            getEnergy().consume(SatelliteConstants.IMAGING_ENERGY_CONSUMPTION);
        } else {
            System.out.println("\uD83D\uDED1 " + getName() + ": Не может выполнить съемку - не активен");
        }
    }

    private void takePhoto() {
        if (getState().getIsActive()) {
            photosTaken++;
            System.out.println(getName() + ": Снимок #" + photosTaken + " сделан!");
        }
    }
}