package ru.bmstu.model.satellite;

import ru.bmstu.constants.SatelliteConstants;

/**
 * <p> Класс, представляющий спутник связи. </p>
 *
 * <p> Наследуется от {@link Satellite} и добавляет специфичную для спутников связи
 * функциональность: пропускную способность и отправку данных. </p>
 *
 * <p>Особенности:</p>
 * <ul>
 *   <li> При выполнении миссии передает данные с заданной скоростью </li>
 *   <li> Расходует энергию согласно {@link SatelliteConstants#COMMUNICATION_ENERGY_CONSUMPTION} </li>
 * </ul>
 */
public class CommunicationSatellite extends Satellite {

    /** <p> Пропускная способность спутника (Мбит/с). </p> */
    private final double bandwidth;

    /**
     * <p> Создает новый спутник связи. </p>
     *
     * @param name имя спутника
     * @param batteryLevel начальный уровень заряда
     * @param bandwidth пропускная способность (Мбит/с)
     */
    public CommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    /**
     * <p> Возвращает пропускную способность спутника. </p>
     *
     * @return пропускная способность в Мбит/с
     */
    public double getBandwidth() {
        return bandwidth;
    }

    /**
     * {@inheritDoc}
     *
     * <p> Выполняет миссию спутника связи: передает данные с заданной скоростью
     * и расходует энергию. </p>
     */
    @Override
    public void performMission() {
        if (state.isActive()) {
            System.out.println(name + ": Передача данных со скоростью " + bandwidth + " Мбит/с");
            sendData(bandwidth);
            energy.consume(SatelliteConstants.COMMUNICATION_ENERGY_CONSUMPTION);
        } else {
            System.out.println("\uD83D\uDED1 " + name + ": Не может выполнить миссию - не активен");
        }
    }

    /**
     * <p> Отправляет указанное количество данных. </p>
     *
     * @param dataAmount количество данных для отправки (Мбит)
     */
    private void sendData(double dataAmount) {
        System.out.println(name + ": Отправил " + dataAmount + " Мбит данных!");
    }

    /**
     * <p> Возвращает строковое представление спутника связи. </p>
     *
     * @return строковое представление со всеми полями
     */
    @Override
    public String toString() {
        return "CommunicationSatellite{" +
                "bandwidth=" + bandwidth +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", energy=" + energy +
                '}';
    }
}