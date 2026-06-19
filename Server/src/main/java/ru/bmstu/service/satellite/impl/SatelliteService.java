package ru.bmstu.service.satellite.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.entity.CommunicationSatelliteEntity;
import ru.bmstu.entity.ImagingSatelliteEntity;
import ru.bmstu.exception.SpaceOperationException;
import ru.bmstu.factory.impl.CommunicationSatelliteFactory;
import ru.bmstu.factory.impl.ImagingSatelliteFactory;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.param.CommunicationSatelliteParam;
import ru.bmstu.param.ImagingSatelliteParam;
import ru.bmstu.param.SatelliteParam;
import ru.bmstu.repository.SatelliteRepository;
import ru.bmstu.service.satellite.ISatelliteService;

import java.util.List;
import java.util.Optional;

@Service
public class SatelliteService implements ISatelliteService {

    private static final Logger log = LoggerFactory.getLogger(SatelliteService.class);

    private final CommunicationSatelliteFactory communicationFactory;
    private final ImagingSatelliteFactory imagingFactory;
    private final SatelliteRepository satelliteRepository;

    public SatelliteService(CommunicationSatelliteFactory communicationFactory,
                            ImagingSatelliteFactory imagingFactory,
                            SatelliteRepository satelliteRepository) {
        this.communicationFactory = communicationFactory;
        this.imagingFactory = imagingFactory;
        this.satelliteRepository = satelliteRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "satellitesAll", allEntries = true)
    public SatelliteEntity createSatellite(SatelliteParam param) {
        String satelliteName = param.getName();

        Optional<SatelliteEntity> existingSatellite = satelliteRepository.findByName(satelliteName);
        if (existingSatellite.isPresent()) {
            log.warn("Спутник с именем {} уже существует, возвращаем существующий", satelliteName);
            return existingSatellite.get();
        }

        SatelliteEntity satellite;
        if (param instanceof CommunicationSatelliteParam) {
            satellite = communicationFactory.createSatelliteEntity(param);
        } else if (param instanceof ImagingSatelliteParam) {
            satellite = imagingFactory.createSatelliteEntity(param);
        } else {
            throw new SpaceOperationException("Неподдерживаемый тип параметров: " + param.getClass().getSimpleName());
        }

        log.info("Создан новый спутник: {}", satelliteName);
        return satelliteRepository.save(satellite);
    }

    @Override
    @Cacheable(value = "satellite", key = "#id")
    public Optional<SatelliteEntity> getSatelliteById(Long id) {
        log.debug("Запрос к БД для спутника с id: {}", id);
        return satelliteRepository.findById(id);
    }

    @Override
    @Cacheable(value = "satellitesAll", key = "'all'")
    public List<SatelliteEntity> getAllSatellites() {
        log.debug("Запрос к БД для списка всех спутников");
        return satelliteRepository.findAll();
    }

    @Override
    @Cacheable(value = "satellite", key = "#constellationName + '::' + #satelliteName")
    public Optional<SatelliteEntity> findByName(String constellationName, String satelliteName) {
        log.debug("Запрос к БД для спутника {} в группировке {}", satelliteName, constellationName);
        return satelliteRepository.findSatellite(constellationName, satelliteName);
    }

    @Override
    @Transactional
    @CacheEvict(value = "satellite", key = "#id")
    public void updateSatellite(Long id, SatelliteParam param) {
        SatelliteEntity satellite = satelliteRepository.findById(id)
                .orElseThrow(() -> new SpaceOperationException("Спутник не найден: " + id));

        satellite.setName(param.getName());

        if (satellite.getEnergy() != null) {
            satellite.getEnergy().setBatteryLevel(param.getBatteryLevel());
        }

        if (satellite instanceof CommunicationSatelliteEntity commSatellite &&
                param instanceof CommunicationSatelliteParam commParam) {
            commSatellite.setBandwidth(commParam.getBandwidth());
        }

        if (satellite instanceof ImagingSatelliteEntity imgSatellite &&
                param instanceof ImagingSatelliteParam imgParam) {
            imgSatellite.setResolution(imgParam.getResolution());
        }

        satelliteRepository.save(satellite);
        log.info("Обновлён спутник с id: {}, новое имя: {}", id, param.getName());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"satellite", "satellitesAll"}, allEntries = true)
    public void deleteSatellite(Long id) {
        SatelliteEntity satellite = satelliteRepository.findById(id)
                .orElseThrow(() -> new SpaceOperationException("Спутник не найден: " + id));

        satelliteRepository.delete(satellite);
        log.info("Удалён спутник с id: {}", id);
    }

    @CacheEvict(value = "satellitesAll", allEntries = true)
    public void evictSatellitesAllCache() {
        log.debug("Инвалидирован кэш satellitesAll");
    }
}