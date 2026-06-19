package ru.bmstu.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.bmstu.scheduler.properties.SpaceCenterProperties;

/**
 * Главный класс приложения-планировщика миссий.
 * <p>
 * Запускает Spring Boot приложение, которое автоматически планирует
 * и выполняет миссии для спутниковых группировок по расписанию.
 * </p>
 */
@SpringBootApplication
@EnableConfigurationProperties(SpaceCenterProperties.class)
public class MissionSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MissionSchedulerApplication.class, args);
    }
}