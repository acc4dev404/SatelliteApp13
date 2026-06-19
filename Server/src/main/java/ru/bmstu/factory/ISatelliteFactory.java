package ru.bmstu.factory;

import ru.bmstu.exception.SpaceOperationException;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.param.SatelliteParam;
import ru.bmstu.param.SatelliteType;

/**
 * <p> Интерфейс фабрики для создания спутников. </p>
 *
 * <p> Реализует паттерн "Фабричный метод" (Factory Method). Каждая конкретная фабрика
 * отвечает за создание спутников определенного типа. </p>
 */
public interface ISatelliteFactory {

    /**
     * <p> Создает JPA-сущность спутника на основе переданных параметров. </p>
     * <p> Фабрика должна проверить, что тип параметра соответствует поддерживаемому,
     * извлечь необходимые поля и создать соответствующий объект спутника. </p>
     *
     * @param param объект с параметрами для создания спутника
     * @return созданная JPA-сущность спутника
     * @throws SpaceOperationException если тип параметра не поддерживается фабрикой или параметры некорректны
     */
    SatelliteEntity createSatelliteEntity(SatelliteParam param);

    /**
     * <p> Проверяет, поддерживает ли фабрика создание спутников указанного типа. </p>
     *
     * @param type тип спутника для проверки
     * @return true если фабрика может создавать спутники этого типа, false в противном случае
     */
    boolean isSatelliteTypeSupported(SatelliteType type);
}