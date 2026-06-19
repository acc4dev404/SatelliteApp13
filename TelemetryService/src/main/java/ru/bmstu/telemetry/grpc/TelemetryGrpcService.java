package ru.bmstu.telemetry.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bmstu.telemetry.kafka.SatelliteEvent;
import ru.bmstu.telemetry.proto.TelemetryRequest;
import ru.bmstu.telemetry.proto.TelemetryServiceGrpc;
import ru.bmstu.telemetry.proto.TelemetryUpdate;
import ru.bmstu.telemetry.service.SatelliteCacheService;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * gRPC сервис для потоковой передачи телеметрии спутников.
 * Эмулирует отправку обновлений каждые 2 секунды.
 */
@GrpcService
public class TelemetryGrpcService extends TelemetryServiceGrpc.TelemetryServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(TelemetryGrpcService.class);
    private static final long UPDATE_INTERVAL_SECONDS = 2;

    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final AtomicBoolean streaming = new AtomicBoolean(false);

    @Autowired
    private SatelliteCacheService satelliteCacheService;

    @Override
    public void streamTelemetry(TelemetryRequest request, StreamObserver<TelemetryUpdate> responseObserver) {
        log.info("gRPC клиент подключился к потоку телеметрии");
        streaming.set(true);

        scheduler.scheduleAtFixedRate(() -> {
            if (!streaming.get()) {
                return;
            }
            try {

                var satellites = satelliteCacheService.getAllSatellites();

                if (satellites.isEmpty()) {
                    log.warn("Нет спутников в кэше, ожидание событий от Kafka...");
                    return;
                }

                for (SatelliteEvent satellite : satellites.values()) {
                    TelemetryUpdate update = generateTelemetryUpdate(satellite.getName());
                    responseObserver.onNext(update);
                    log.debug("📡 Отправлена телеметрия для {}: внутр.={:.1f}°C, внеш.={:.1f}°C, напр.={:.1f}В",
                            satellite.getName(),
                            update.getInternalTemperature(),
                            update.getExternalTemperature(),
                            update.getBatteryVoltage());
                }
            } catch (Exception e) {
                log.error("Ошибка при отправке телеметрии: {}", e.getMessage());
                streaming.set(false);
                responseObserver.onError(e);
            }
        }, 0, UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.warn("Поток телеметрии прерван");
            streaming.set(false);
            responseObserver.onCompleted();
            scheduler.shutdown();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Генерирует случайные показатели телеметрии для спутника.
     *
     * @param satelliteName имя спутника
     * @return TelemetryUpdate со сгенерированными данными
     */
    private TelemetryUpdate generateTelemetryUpdate(String satelliteName) {
        return TelemetryUpdate.newBuilder()
                .setSatelliteName(satelliteName)
                .setInternalTemperature(15 + random.nextDouble() * 20)      // 15-35°C
                .setExternalTemperature(-50 + random.nextDouble() * 100)    // -50 до +50°C
                .setBatteryVoltage(24 + random.nextDouble() * 8)            // 24-32В
                .setTimestamp(System.currentTimeMillis())
                .build();
    }
}