package ru.bmstu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "constellations", indexes = {
        @Index(name = "idx_constellation_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
public class ConstellationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "constellation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SatelliteEntity> satellites = new ArrayList<>();

    public ConstellationEntity(String name) {
        this.name = name;
    }

    /**
     * <p> Добавляет спутник в группировку. </p>
     *
     * @param satellite спутник для добавления
     */
    public void addSatellite(SatelliteEntity satellite) {
        satellites.add(satellite);
        satellite.setConstellation(this);
    }

    /**
     * <p> Удаляет спутник из группировки. </p>
     *
     * @param satelliteName имя спутника для удаления
     * @return true если спутник был удалён, false если не найден
     */
    public boolean removeSatellite(String satelliteName) {
        return satellites.removeIf(satellite -> satellite.getName().equals(satelliteName));
    }

    /**
     * <p> Возвращает список спутников в группировке. </p>
     *
     * @return список спутников
     */
    public List<SatelliteEntity> getSatellites() {
        return satellites;
    }

    /**
     * <p> Возвращает название группировки. </p>
     *
     * @return название группировки
     */
    public String getName() {
        return name;
    }
}