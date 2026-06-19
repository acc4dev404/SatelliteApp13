package ru.bmstu.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.entity.SatelliteEntity;
import ru.bmstu.repository.SatelliteRepository;
import ru.bmstu.telemetry.proto.TelemetryRequest;
import ru.bmstu.telemetry.proto.TelemetryServiceGrpc;
import ru.bmstu.telemetry.proto.TelemetryUpdate;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TelemetryUpdateService {

    private static final Logger log = LoggerFactory.getLogger(TelemetryUpdateService.class);

    @GrpcClient("telemetry-service")
    private TelemetryServiceGrpc.TelemetryServiceStub telemetryStub;

    private final SatelliteRepository satelliteRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean streaming = new AtomicBoolean(false);

    public TelemetryUpdateService(SatelliteRepository satelliteRepository) {
        this.satelliteRepository = satelliteRepository;
    }

    @PostConstruct
    public void startTelemetryStream() {
        log.info("Запуск gRPC клиента для получения телеметрии...");

        streaming.set(true);

        StreamObserver<TelemetryUpdate> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(TelemetryUpdate update) {
                log.debug("Получена телеметрия: {} - внутр.={}°C, внеш.={}°C, напр.={}В",
                        update.getSatelliteName(),
                        String.format("%.1f", update.getInternalTemperature()),
                        String.format("%.1f", update.getExternalTemperature()),
                        String.format("%.1f", update.getBatteryVoltage()));

                updateSatelliteTelemetry(update);
            }

            @Override
            public void onError(Throwable t) {
                log.error("Ошибка в gRPC потоке телеметрии: {}", t.getMessage());
                streaming.set(false);
                executor.submit(() -> {
                    try {
                        Thread.sleep(5000);
                        if (!streaming.get()) {
                            log.info("Попытка переподключения к gRPC серверу...");
                            startTelemetryStream();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            @Override
            public void onCompleted() {
                log.info("gRPC поток телеметрии завершён");
                streaming.set(false);
            }
        };

        telemetryStub.streamTelemetry(
                TelemetryRequest.newBuilder().setStreamAll(true).build(),
                responseObserver
        );

        log.info("gRPC клиент телеметрии запущен");
    }

    /**
     * Обновление телеметрии спутника.
     * @Transactional обеспечивает открытую транзакцию для загрузки lazy-связей
     */
    @Async("telemetryExecutor")
    @Transactional
    public void updateSatelliteTelemetry(TelemetryUpdate update) {
        String satelliteName = update.getSatelliteName();

        try {

            Optional<SatelliteEntity> satelliteOpt = satelliteRepository.findByNameWithDetails(satelliteName);

            if (satelliteOpt.isEmpty()) {
                log.warn("Спутник {} не найден в БД", satelliteName);
                return;
            }

            SatelliteEntity satellite = satelliteOpt.get();

            satellite.setInternalTemperature(update.getInternalTemperature());
            satellite.setExternalTemperature(update.getExternalTemperature());
            satellite.setBatteryVoltage(update.getBatteryVoltage());
            satellite.setLastTelemetryTime(Instant.now());

            satelliteRepository.save(satellite);

            log.info("Обновлена телеметрия для {}: заряд={}%, внутр.={:.1f}°C, внеш.={:.1f}°C, напр.={:.1f}В",
                    satellite.getName(),
                    (int)(satellite.getEnergy().getBatteryLevel() * 100),
                    update.getInternalTemperature(),
                    update.getExternalTemperature(),
                    update.getBatteryVoltage());

        } catch (Exception e) {
            log.error("Ошибка при обновлении телеметрии для {}: {}", satelliteName, e.getMessage(), e);
        }
    }

    @PreDestroy
    public void stopTelemetryStream() {
        log.info("Остановка gRPC клиента телеметрии...");
        streaming.set(false);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}