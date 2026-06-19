# 🛰️ Space Operation Center (Server)
## Основной сервис управления спутниковыми группировками

---

## Общая информация

| Параметр | Значение |
|----------|----------|
| **Название** | Space Operation Center |
| **Порт** | 8080 |
| **Контекст** | `/api` |
| **Основной класс** | `ru.bmstu.Main` |
| **Тип хранилища** | In-memory (HashMap) |
| **Java версия** | 21 |
| **Spring Boot** | 3.4.2 |

---

## 🎯 Функциональные возможности

- ✅ Создание спутниковых группировок
- ✅ Добавление спутников связи (`COMMUNICATION`) и ДЗЗ (`IMAGING`)
- ✅ Выполнение миссий для группировок
- ✅ Получение статуса системы
- ✅ Вывод спутников из эксплуатации
- ✅ AOP логирование времени выполнения методов
- ✅ Swagger/OpenAPI документация

---

## 🚀 Запуск

```bash
# Сборка проекта
./gradlew clean build

# Запуск приложения
./gradlew bootRun

# Или через JAR
java -jar build/libs/Server-1.0-SNAPSHOT.jar
```

После запуска:
- Приложение доступно на `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI спецификация: `http://localhost:8080/v3/api-docs`

---

## 📁 Структура проекта

```
Server/
├── src/main/java/ru/bmstu/
│   ├── Main.java                          # Точка входа
│   │
│   ├── annotation/                        # Кастомные аннотации
│   │   └── LogExecutionTime.java          # Аспект для замера времени
│   │
│   ├── aspect/                            # AOP аспекты
│   │   └── LoggingAspect.java             # Логирование времени выполнения
│   │
│   ├── constants/                         # Константы
│   │   ├── EnergySystemConstants.java     # Пороги заряда батареи
│   │   └── SatelliteConstants.java        # Энергопотребление спутников
│   │
│   ├── controller/                        # REST контроллеры
│   │   └── SpaceOperationController.java  # API эндпоинты
│   │
│   ├── demo/                              # Демо-запуск
│   │   └── SatelliteSystemDemo.java       # Командная строка при старте
│   │
│   ├── dto/                               # DTO для API
│   │   ├── AddSatelliteRequest.java
│   │   ├── ConstellationStatusResponse.java
│   │   ├── CreateConstellationRequest.java
│   │   └── MissionRequest.java
│   │
│   ├── exception/                         # Исключения
│   │   └── SpaceOperationException.java
│   │
│   ├── factory/                           # Фабрики (паттерн Factory Method)
│   │   ├── ISatelliteFactory.java
│   │   └── impl/
│   │       ├── CommunicationSatelliteFactory.java
│   │       └── ImagingSatelliteFactory.java
│   │
│   ├── model/                             # Модели данных
│   │   ├── constellation/
│   │   │   └── SatelliteConstellation.java
│   │   └── satellite/
│   │       ├── CommunicationSatellite.java
│   │       ├── EnergySystem.java
│   │       ├── ImagingSatellite.java
│   │       ├── Satellite.java
│   │       └── SatelliteState.java
│   │
│   ├── param/                             # Параметры (полиморфные DTO)
│   │   ├── CommunicationSatelliteParam.java
│   │   ├── ImagingSatelliteParam.java
│   │   ├── SatelliteParam.java
│   │   └── SatelliteType.java
│   │
│   ├── repository/                        # Репозиторий (in-memory)
│   │   └── ConstellationRepository.java
│   │
│   └── service/                           # Сервисы
│       ├── ConstellationService.java
│       ├── SpaceOperationCenterFacade.java
│       └── satellite/
│           ├── ISatelliteService.java
│           └── impl/
│               └── SatelliteService.java
│
├── src/main/resources/
│   └── application.yaml                   # Конфигурация
│
└── build.gradle.kts                       # Сборка Gradle
```

---

## 🔌 API Эндпоинты

### 1. Управление группировками

#### `POST /api/constellations` — Создание группировки

Создаёт новую группировку и добавляет в неё спутники.

**Request Body:**
```json
{
  "constellationName": "Орбита-1",
  "satelliteParams": [
    {
      "type": "COMMUNICATION",
      "name": "Связь-1",
      "batteryLevel": 0.85,
      "bandwidth": 500.0
    },
    {
      "type": "IMAGING", 
      "name": "ДЗЗ-1",
      "batteryLevel": 0.92,
      "resolution": 2.5
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "constellationName": "Орбита-1",
  "message": "Группировка успешно создана"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/constellations \
  -H "Content-Type: application/json" \
  -d '{
    "constellationName": "Орбита-1",
    "satelliteParams": [
      {"type": "COMMUNICATION", "name": "Связь-1", "batteryLevel": 0.85, "bandwidth": 500},
      {"type": "IMAGING", "name": "ДЗЗ-1", "batteryLevel": 0.92, "resolution": 2.5}
    ]
  }'
```

---

#### `POST /api/constellations/satellites` — Добавление спутника

Добавляет новый спутник в существующую группировку.

**Request Body:**
```json
{
  "constellationName": "Орбита-1",
  "satelliteParam": {
    "type": "IMAGING",
    "name": "ДЗЗ-3",
    "batteryLevel": 0.65,
    "resolution": 1.2
  }
}
```

**Response (200 OK):**
```json
{
  "message": "Спутник успешно добавлен в группировку Орбита-1"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/constellations/satellites \
  -H "Content-Type: application/json" \
  -d '{
    "constellationName": "Орбита-1",
    "satelliteParam": {"type": "IMAGING", "name": "ДЗЗ-3", "batteryLevel": 0.65, "resolution": 1.2}
  }'
```

---

#### `DELETE /api/constellations/{constellationName}/satellites/{satelliteName}` — Удаление спутника

Выводит спутник из эксплуатации (удаляет из группировки).

**Path Parameters:**
- `constellationName` — название группировки
- `satelliteName` — имя спутника

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Спутник Связь-1 успешно удален из группировки Орбита-1"
}
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Спутник Связь-1 не найден в группировке Орбита-1"
}
```

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/constellations/Орбита-1/satellites/Связь-1
```

---

### 2. Выполнение миссий

#### `POST /api/missions` — Выполнение миссий

Выполняет миссии для указанных группировок.

**Request Body:**
```json
{
  "constellationNames": ["Орбита-1", "Орбита-2"],
  "activateBeforeMission": true,
  "showStatusAfterMission": true
}
```

| Поле | Тип | Описание |
|------|-----|----------|
| `constellationNames` | List<String> | Список группировок для миссии |
| `activateBeforeMission` | boolean | Активировать спутники перед выполнением |
| `showStatusAfterMission` | boolean | Показать статус после выполнения |

**Response:** `200 OK` (без тела)

**cURL:**
```bash
curl -X POST http://localhost:8080/api/missions \
  -H "Content-Type: application/json" \
  -d '{"constellationNames": ["Орбита-1"], "activateBeforeMission": true, "showStatusAfterMission": true}'
```

---

#### `POST /api/missions/full-cycle` — Полный цикл миссии

Создаёт группировку → активирует спутники → выполняет миссии → возвращает статус.

**Request Body:** (как при создании группировки)

**Response (200 OK):**
```json
{
  "constellationName": "Орбита-2",
  "satelliteCount": 2,
  "satelliteStatuses": {
    "Связь-3": "Активен",
    "ДЗЗ-4": "Активен"
  },
  "batteryLevels": {
    "Связь-3": 0.95,
    "ДЗЗ-4": 0.88
  }
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/missions/full-cycle \
  -H "Content-Type: application/json" \
  -d '{
    "constellationName": "Орбита-2",
    "satelliteParams": [
      {"type": "COMMUNICATION", "name": "Связь-3", "batteryLevel": 0.95, "bandwidth": 2000},
      {"type": "IMAGING", "name": "ДЗЗ-4", "batteryLevel": 0.88, "resolution": 0.8}
    ]
  }'
```

---

### 3. Получение информации

#### `GET /api/constellations/status` — Статус всех группировок

**Response (200 OK):**
```json
{
  "Орбита-1": {
    "constellationName": "Орбита-1",
    "satelliteCount": 4,
    "satelliteStatuses": {
      "Связь-1": "Активен",
      "Связь-2": "Не активен",
      "ДЗЗ-1": "Активен",
      "ДЗЗ-2": "Деактивирован"
    },
    "batteryLevels": {
      "Связь-1": 0.75,
      "Связь-2": 0.45,
      "ДЗЗ-1": 0.82,
      "ДЗЗ-2": 0.15
    }
  }
}
```

**cURL:**
```bash
curl http://localhost:8080/api/constellations/status
```

---

#### `GET /api/constellations/{name}/status` — Статус конкретной группировки

**Path Parameters:**
- `name` — название группировки

**Response (200 OK):** (как элемент выше)

**cURL:**
```bash
curl http://localhost:8080/api/constellations/Орбита-1/status
```

---

#### `GET /api/overview` — Сводка по системе

Возвращает агрегированную статистику по всей системе.

**Response (200 OK):**
```json
{
  "totalConstellations": 2,
  "totalSatellites": 7,
  "activeSatellites": 4,
  "inactiveSatellites": 3,
  "criticalBatterySatellites": 1,
  "constellations": ["Орбита-1", "Орбита-2"]
}
```

| Поле | Описание |
|------|----------|
| `totalConstellations` | Общее количество группировок |
| `totalSatellites` | Общее количество спутников |
| `activeSatellites` | Количество активных спутников |
| `inactiveSatellites` | Количество неактивных спутников |
| `criticalBatterySatellites` | Спутники с зарядом < 20% |
| `constellations` | Список названий группировок |

**cURL:**
```bash
curl http://localhost:8080/api/overview
```

---

### 4. Health Check

#### `GET /actuator/health` — Проверка доступности сервиса

**Response (200 OK):**
```json
{
  "status": "UP"
}
```

**cURL:**
```bash
curl http://localhost:8080/actuator/health
```

---

## Модели данных

### Типы спутников

| Тип | Параметры | Энергопотребление | Особенности |
|-----|-----------|-------------------|-------------|
| **COMMUNICATION** | `bandwidth` (Мбит/с) | 5% за миссию | Передача данных |
| **IMAGING** | `resolution` (м/пиксель) | 8% за миссию | Съёмка территории |

### DTO Структуры

#### CreateConstellationRequest
```java
{
    String constellationName;
    List<SatelliteParam> satelliteParams;
}
```

#### SatelliteParam (абстрактный, полиморфный)
```java
{
    String type;           // "COMMUNICATION" или "IMAGING"
    String name;
    double batteryLevel;   // от 0.0 до 1.0
    // + специфичные поля в зависимости от type
}
```

#### MissionRequest
```java
{
    List<String> constellationNames;
    boolean activateBeforeMission;
    boolean showStatusAfterMission;
}
```

#### ConstellationStatusResponse
```java
{
    String constellationName;
    int satelliteCount;
    Map<String, String> satelliteStatuses;  // имя → статус
    Map<String, Double> batteryLevels;      // имя → уровень заряда
}
```

---

## Внутренняя архитектура

### Паттерны проектирования

| Паттерн | Где используется | Назначение |
|---------|------------------|------------|
| **Facade** | `SpaceOperationCenterFacade` | Упрощённый интерфейс для сложной подсистемы |
| **Factory Method** | `ISatelliteFactory`, `CommunicationSatelliteFactory`, `ImagingSatelliteFactory` | Создание спутников разных типов |
| **Repository** | `ConstellationRepository` | Абстракция хранения группировок |
| **Builder** | `EnergySystem.builder()`, `SatelliteConstellation.builder()` | Пошаговое создание сложных объектов |
| **Strategy** | `ISatelliteService` | Выбор фабрики в зависимости от типа |
| **AOP** | `LoggingAspect` | Логирование времени выполнения методов |

---

## Конфигурация (`application.yaml`)

```yaml
server:
  port: 8080

spring:
  application:
    name: space-operation-center

logging:
  level:
    ru.bmstu: DEBUG
```

---

## Логирование

### Аспектное логирование (`@LogExecutionTime`)

```java
@LogExecutionTime(threshold = 50)  // предупреждение если > 50 мс
public void createAndSaveConstellation(String name) { ... }
```

**Вывод в консоль:**
```
[INFO] ConstellationService.createAndSaveConstellation() выполнен за 12.34 мс
[WARNING] ConstellationService.executeConstellationMission() выполнен за 156.78 мс
```

### Демо-запуск при старте

При запуске приложения автоматически выполняется демонстрационный сценарий:

1. Создание группировки "Орбита-1" с 4 спутниками
2. Добавление спутника "ДЗЗ-3"
3. Выполнение миссий
4. Получение статуса
5. Полный цикл миссии для "Орбита-2"

---

## Тестирование

### Запуск тестов

```bash
./gradlew test
```

### Пример интеграционного теста

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SpaceOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateConstellation() throws Exception {
        String request = """
            {
                "constellationName": "Тест",
                "satelliteParams": [
                    {"type": "COMMUNICATION", "name": "Сат-1", "batteryLevel": 0.8, "bandwidth": 100}
                ]
            }
            """;

        mockMvc.perform(post("/api/constellations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constellationName").value("Тест"));
    }
}
```
---

## Зависимости (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
}
```