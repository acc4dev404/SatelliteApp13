package ru.bmstu.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.bmstu.dto.AddSatelliteRequest;
import ru.bmstu.dto.CreateConstellationRequest;
import ru.bmstu.dto.MissionRequest;
import ru.bmstu.param.CommunicationSatelliteParam;
import ru.bmstu.param.ImagingSatelliteParam;
import ru.bmstu.param.SatelliteParam;
import ru.bmstu.service.SpaceOperationCenterFacade;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true", matchIfMissing = true)
public class SatelliteSystemDemo implements CommandLineRunner {

    private final SpaceOperationCenterFacade facade;

    public SatelliteSystemDemo(SpaceOperationCenterFacade facade) {
        this.facade = facade;
    }

    @Override
    public void run(String... args) {
        // Проверяем, есть ли уже данные в системе
        if (isDataAlreadyExists()) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("⚠️ ДАННЫЕ УЖЕ СУЩЕСТВУЮТ - ПРОПУСК ДЕМО");
            System.out.println("=".repeat(70));
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("🚀 ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ (FACADE)");
        System.out.println("=".repeat(70));

        // Демонстрация 1: Создание группировки через Facade
        System.out.println("\n📡 ДЕМО 1: Создание группировки через фасад");
        System.out.println("-".repeat(50));

        List<SatelliteParam> orbit1Params = Arrays.asList(
                new CommunicationSatelliteParam("Связь-1", 0.85, 500),
                new CommunicationSatelliteParam("Связь-2", 0.75, 1000),
                new ImagingSatelliteParam("ДЗЗ-1", 0.92, 2.5),
                new ImagingSatelliteParam("ДЗЗ-2", 0.45, 1.0)
        );

        CreateConstellationRequest createRequest = new CreateConstellationRequest(
                "Орбита-1",
                orbit1Params
        );

        String constellationName = facade.createConstellationWithSatellites(createRequest);

        // Демонстрация 2: Добавление спутника через Facade
        System.out.println("\n📡 ДЕМО 2: Добавление спутника через фасад");
        System.out.println("-".repeat(50));

        AddSatelliteRequest addRequest = new AddSatelliteRequest(
                "Орбита-1",
                new ImagingSatelliteParam("ДЗЗ-3", 0.15, 0.5)
        );

        facade.addSatelliteToConstellation(addRequest);

        // Демонстрация 3: Выполнение миссий через Facade
        System.out.println("\n📡 ДЕМО 3: Выполнение миссий через фасад");
        System.out.println("-".repeat(50));

        MissionRequest missionRequest = new MissionRequest(
                Arrays.asList("Орбита-1"),
                true,
                true
        );

        facade.executeMissions(missionRequest);

        // Демонстрация 4: Получение статуса через Facade
        System.out.println("\n📡 ДЕМО 4: Получение статуса через фасад");
        System.out.println("-".repeat(50));

        System.out.println("Статус группировки 'Орбита-1':");
        System.out.println(facade.getConstellationStatus("Орбита-1"));

        // Демонстрация 5: Полный цикл миссии для новой группировки
        System.out.println("\n📡 ДЕМО 5: Полный цикл миссии для новой группировки");
        System.out.println("-".repeat(50));

        List<SatelliteParam> orbit2Params = Arrays.asList(
                new CommunicationSatelliteParam("Связь-3", 0.95, 2000),
                new ImagingSatelliteParam("ДЗЗ-4", 0.88, 0.8)
        );

        CreateConstellationRequest fullCycleRequest = new CreateConstellationRequest(
                "Орбита-2",
                orbit2Params
        );

        var finalStatus = facade.runFullMissionCycle(fullCycleRequest);
        System.out.println("\nИтоговый статус после полного цикла:");
        System.out.println(finalStatus);

        System.out.println("\n" + "=".repeat(70));
        System.out.println("✅ ДЕМОНСТРАЦИЯ ЗАВЕРШЕНА");
        System.out.println("=".repeat(70));
    }

    private boolean isDataAlreadyExists() {
        try {
            // Проверяем, есть ли хотя бы одна группировка в БД
            return facade.hasAnyConstellation();
        } catch (Exception e) {
            System.err.println("Ошибка при проверке существования данных: " + e.getMessage());
            return true;
        }
    }
}