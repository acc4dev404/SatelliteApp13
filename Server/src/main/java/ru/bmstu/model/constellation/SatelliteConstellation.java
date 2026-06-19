package ru.bmstu.model.constellation;

import lombok.Getter;
import lombok.ToString;
import ru.bmstu.model.satellite.Satellite;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> Класс, представляющий группировку спутников. </p>
 *
 * <p> Содержит коллекцию спутников и предоставляет методы для управления группой:
 * добавление спутников, выполнение миссий всех спутников одновременно. </p>
 *
 * <p> Пример использования: </p>
 * <pre>{@code
 * SatelliteConstellation constellation = new SatelliteConstellation("Орбита-1");
 * constellation.addSatellite(commSat1);
 * constellation.addSatellite(imgSat1);
 * constellation.executeAllMissions();
 * }</pre>
 */
@Getter
@ToString
public class SatelliteConstellation {

    /** <p> Название группировки. </p> */
    private final String constellationName;

    /** <p> Список спутников в группировке. </p> */
    private final List<Satellite> satellites;

    /**
     * <p> Создает новую пустую группировку спутников. </p>
     *
     * @param constellationName название группировки
     */
    public SatelliteConstellation(String constellationName) {
        if (constellationName == null || constellationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название группировки не может быть пустым");
        }
        this.constellationName = constellationName;
        this.satellites = new ArrayList<>();
        System.out.println("Создана спутниковая группировка: " + constellationName);
    }

    /**
     * <p> Приватный конструктор для Builder. </p>
     *
     * <p> Создает группировку с предустановленным списком спутников. </p>
     *
     * @param constellationName название группировки
     * @param satellites список спутников
     */
    private SatelliteConstellation(String constellationName, List<Satellite> satellites) {
        this.constellationName = constellationName;
        this.satellites = new ArrayList<>(satellites);
        System.out.println("Создана спутниковая группировка: " + constellationName + " с " + satellites.size() + " спутниками");
    }

    /**
     * <p> Добавляет спутник в группировку. </p>
     *
     * <p> Проверяет, что спутник не null и еще не добавлен в группировку. </p>
     *
     * @param satellite спутник для добавления
     */
    public void addSatellite(Satellite satellite) {
        if (satellite != null && !satellites.contains(satellite)) {
            satellites.add(satellite);
            System.out.println(satellite.getName() + " добавлен в группировку '" + constellationName + "'");
        }
    }

    /**
     * <p> Выполняет миссии всех спутников в группировке. </p>
     *
     * <p> Для каждого спутника вызывается его метод {@link Satellite#performMission()}. </p>
     */
    public void executeAllMissions() {
        System.out.println("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ " + constellationName.toUpperCase());
        System.out.println("=".repeat(50));

        for (Satellite satellite : satellites) {
            satellite.performMission();
        }
    }

    /**
     * <p> Создает новый экземпляр Builder для SatelliteConstellation. </p>
     *
     * @param constellationName название группировки
     * @return новый экземпляр ConstellationBuilder
     */
    public static ConstellationBuilder builder(String constellationName) {
        return new ConstellationBuilder(constellationName);
    }

    /**
     * Удаляет спутник из группировки.
     *
     * @param satelliteName имя спутника для удаления
     * @return true если спутник был удалён, false если не найден
     */
    public boolean removeSatellite(String satelliteName) {
        return satellites.removeIf(satellite -> satellite.getName().equals(satelliteName));
    }

    /**
     * <p> Внутренний класс Builder для пошагового создания SatelliteConstellation. </p>
     */
    public static class ConstellationBuilder {
        private final String constellationName;
        private final List<Satellite> satellites = new ArrayList<>();

        private ConstellationBuilder(String constellationName) {
            this.constellationName = constellationName;
        }

        /**
         * <p> Добавляет спутник в группировку. </p>
         *
         * @param satellite спутник для добавления
         * @return этот Builder для цепочечных вызовов
         */
        public ConstellationBuilder addSatellite(Satellite satellite) {
            this.satellites.add(satellite);
            return this;
        }

        /**
         * <p> Добавляет несколько спутников в группировку. </p>
         *
         * @param satellites список спутников для добавления
         * @return этот Builder для цепочечных вызовов
         */
        public ConstellationBuilder addSatellites(List<Satellite> satellites) {
            this.satellites.addAll(satellites);
            return this;
        }

        /**
         * <p> Создает экземпляр SatelliteConstellation с настроенными параметрами. </p>
         *
         * @return новый экземпляр SatelliteConstellation
         */
        public SatelliteConstellation build() {
            if (satellites.isEmpty()) {
                return new SatelliteConstellation(constellationName);
            }
            return new SatelliteConstellation(constellationName, satellites);
        }

    }
}