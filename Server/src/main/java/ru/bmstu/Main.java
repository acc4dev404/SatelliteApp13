package ru.bmstu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Главный класс приложения для управления спутниковыми группировками.
 * <p> Запускает Spring Boot приложение, которое инициализирует контекст,
 * создает все необходимые бины. </p>
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Main {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}