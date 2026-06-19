package ru.bmstu.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.bmstu.scheduler.properties.SpaceCenterProperties;

/**
 * Конфигурация HTTP-клиента для взаимодействия с основным сервисом.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient spaceOperationRestClient(SpaceCenterProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.url())
                .build();
    }
}