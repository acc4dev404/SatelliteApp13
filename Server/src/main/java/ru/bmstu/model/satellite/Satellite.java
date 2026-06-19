package ru.bmstu.model.satellite;

/**
 * <p> Абстрактный базовый класс для всех типов спутников. </p>
 *
 * <p> Реализует общую функциональность спутников: хранение имени, управление состоянием,
 * управление энергией, активацию/деактивацию. Конкретные типы спутников должны
 * реализовать метод {@link #performMission()}. </p>
 *
 * <p> Принципы SOLID, реализованные в этом классе: </p>
 * <ul>
 *   <li> <b>SRP</b> - класс делегирует управление состоянием и энергией отдельным классам </li>
 *   <li> <b>OCP</b> - класс открыт для расширения (новые типы спутников) но закрыт для модификации </li>
 *   <li> <b>LSP</b> - все наследники могут быть использованы вместо базового класса </li>
 * </ul>
 */
public abstract class Satellite {

    /** <p> Имя спутника. </p> */
    protected String name;

    /** <p> Текущее состояние спутника (активен/не активен). </p> */
    protected SatelliteState state;

    /** <p> Система управления энергией спутника. </p> */
    protected EnergySystem energy;

    /**
     * <p> Создает новый спутник с указанным именем и уровнем заряда. </p>
     *
     * <p> Автоматически создает {@link EnergySystem} через Builder и
     * инициализирует состояние спутника как неактивное. </p>
     *
     * @param name имя спутника (не может быть null)
     * @param batteryLevel начальный уровень заряда (будет автоматически ограничен)
     * @throws IllegalArgumentException если name null или пуст
     */
    public Satellite(String name, double batteryLevel) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя спутника не может быть пустым");
        }
        this.name = name;
        this.energy = EnergySystem.builder()
                .batteryLevel(batteryLevel)
                .build();
        this.state = new SatelliteState();
        System.out.println("Создан спутник: " + name + " (" + energy.getBatteryLevel() + ")");
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
     * <p> Возвращает текущее состояние спутника. </p>
     *
     * @return объект состояния спутника
     */
    public SatelliteState getState() {
        return state;
    }

    /**
     * <p> Возвращает систему управления энергией спутника. </p>
     *
     * @return объект EnergySystem
     */
    public EnergySystem getEnergy() {
        return energy;
    }

    /**
     * <p> Активирует спутник, если достаточно энергии. </p>
     *
     * <p> Делегирует проверку достаточности энергии и активацию
     * соответствующим классам {@link EnergySystem} и {@link SatelliteState}. </p>
     *
     * @return true если активация успешна, false в противном случае
     */
    public boolean activate() {
        if (state.activate(energy.hasSufficientPower())) {
            System.out.println("✅ " + name + ": Активация успешна");
            return true;
        }
        System.out.println("\uD83D\uDED1 " + name + ": Ошибка активации (заряд: " +
                (int)(getEnergy().getBatteryLevel() * 100) + "%)");
        return false;
    }

    /**
     * <p> Деактивирует спутник, если он активен. </p>
     */
    public void deactivate() {
        if (state.isActive()) {
            state.deactivate();
            System.out.println("\uD83D\uDED1 " + name + ": Деактивирован");
        }
    }

    /**
     * <p> Абстрактный метод для выполнения миссии спутника. </p>
     *
     * <p> Должен быть реализован в классах-наследниках с учетом специфики
     * конкретного типа спутника (связь, съемка и т.д.). </p>
     */
    public abstract void performMission();
}