package ru.bmstu.model.satellite;

import ru.bmstu.constants.SatelliteConstants;

/**
 * <p> Класс, представляющий спутник дистанционного зондирования Земли (ДЗЗ). </p>
 *
 * <p> Наследуется от {@link Satellite} и добавляет специфичную для спутников ДЗЗ
 * функциональность: разрешение съемки и подсчет сделанных снимков. </p>
 *
 * <p> Особенности: </p>
 * <ul>
 *   <li> При выполнении миссии делает снимок территории </li>
 *   <li> Ведет подсчет количества сделанных снимков </li>
 *   <li> Расходует энергию согласно {@link SatelliteConstants#IMAGING_ENERGY_CONSUMPTION} </li>
 * </ul>
 */
public class ImagingSatellite extends Satellite {

    /** <p> Разрешение съемки (метров на пиксель). </p> */
    private final double resolution;

    /** <p> Количество сделанных снимков. </p> */
    private int photosTaken;

    /**
     * <p> Возвращает разрешение съемки спутника. </p>
     *
     * @return разрешение в метрах на пиксель
     */
    public double getResolution() {
        return resolution;
    }

    /**
     * <p> Возвращает количество сделанных снимков. </p>
     *
     * @return количество снимков
     */
    public int getPhotosTaken() {
        return photosTaken;
    }

    /**
     * <p> Создает новый спутник ДЗЗ. </p>
     *
     * @param name имя спутника
     * @param batteryLevel начальный уровень заряда
     * @param resolution разрешение съемки (метров на пиксель)
     */
    public ImagingSatellite(String name, double batteryLevel, double resolution) {
        super(name, batteryLevel);
        this.resolution = resolution;
        this.photosTaken = 0;
    }

    /**
     * {@inheritDoc}
     * <p> Выполняет миссию спутника ДЗЗ: делает снимок территории с заданным разрешением,
     * увеличивает счетчик снимков и расходует энергию. </p>
     */
    @Override
    public void performMission() {
        if (state.isActive()) {
            System.out.println(name + ": Съемка территории с разрешением " + resolution + " м/пиксель");
            takePhoto();
            energy.consume(SatelliteConstants.IMAGING_ENERGY_CONSUMPTION);
        } else {
            System.out.println("\uD83D\uDED1 " + name + ": Не может выполнить съемку - не активен");
        }
    }

    /**
     * <p> Делает снимок, если спутник активен. </p>
     *
     * <p> Увеличивает счетчик {@link #photosTaken} на 1. </p>
     */
    private void takePhoto() {
        if (state.isActive()) {
            photosTaken++;
            System.out.println(name + ": Снимок #" + photosTaken + " сделан!");
        }
    }

    /**
     * <p> Возвращает строковое представление спутника ДЗЗ. </p>
     *
     * @return строковое представление со всеми полями
     */
    @Override
    public String toString() {
        return "ImagingSatellite{" +
                "resolution=" + resolution +
                ", photosTaken=" + photosTaken +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", energy=" + energy +
                '}';
    }
}