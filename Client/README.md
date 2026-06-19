# ⏰ Mission Scheduler (Client)
## Сервис-планировщик автоматических миссий

---

## Общая информация

| Параметр | Значение |
|----------|----------|
| **Название** | Mission Scheduler |
| **Порт** | 8081 |
| **Контекст** | `/api/scheduler` |
| **Основной класс** | `ru.bmstu.scheduler.MissionSchedulerApplication` |
| **Пул потоков** | 5 |
| **Тип планирования** | Cron (6 полей, включая секунды) |
| **Java версия** | 21 |
| **Spring Boot** | 3.4.2 |

---

## Функциональные возможности

- ✅ Чтение миссий из `application.yaml` при старте
- ✅ Автоматический запуск по cron-расписанию
- ✅ Ручной запуск миссий через REST API
- ✅ Просмотр списка запланированных миссий
- ✅ Статистика выполнения
- ✅ Health check (проверка доступности основного сервиса)
- ✅ Graceful shutdown (завершение запущенных задач)
- ✅ Асинхронное выполнение в пуле потоков

---

## 🚀 Запуск

```bash
# Сборка проекта
./gradlew clean build

# Запуск приложения
./gradlew bootRun

# Или через JAR
java -jar build/libs/Client-1.0-SNAPSHOT.jar
```

**Важно:** перед запуском планировщика должен быть запущен основной сервис (Server) на порту 8080.

После запуска:
- Приложение доступно на `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI спецификация: `http://localhost:8081/v3/api-docs`

---

## 📁 Структура проекта

```
Client/
├── src/main/java/ru/bmstu/scheduler/
│   ├── MissionSchedulerApplication.java   # Точка входа
│   │
│   ├── client/                            # HTTP клиенты
│   │   └── SpaceOperationClient.java      # Взаимодействие с Server
│   │
│   ├── config/                            # Конфигурации
│   │   ├── RestClientConfig.java          # RestClient бин
│   │   └── SchedulerConfig.java           # TaskScheduler с пулом потоков
│   │
│   ├── controller/                        # REST контроллеры
│   │   └── SpaceOperationController.java  # API эндпоинты
│   │
│   ├── dto/                               # DTO для API
│   │   ├── MissionRequest.java            # Запрос для группировки
│   │   └── SingleSatelliteMissionRequest.java  # Запрос для спутника
│   │
│   ├── exception/                         # Обработка ошибок
│   │   └── SchedulerExceptionHandler.java # Глобальный хендлер
│   │
│   ├── properties/                        # Конфигурационные свойства
│   │   └── SpaceCenterProperties.java     # @ConfigurationProperties
│   │
│   └── service/                           # Сервисы
│       └── MissionSchedulerService.java   # Планирование миссий
│
├── src/main/resources/
│   └── application.yaml                   # Конфигурация миссий
│
└── build.gradle.kts                       # Сборка Gradle
```

---

## Конфигурация (`application.yaml`)

```yaml
server:
  port: 8081

spring:
  application:
    name: mission-scheduler

app:
  space-center-service:
    url: "http://localhost:8080/api"
    missions:
      - targetType: CONSTELLATION
        constellationName: "Орбита-1"
        cron: "0 */1 * * * *"           # каждую минуту

      - targetType: CONSTELLATION
        constellationName: "Орбита-2"
        cron: "0 */2 * * * *"           # каждые 2 минуты

      - targetType: SINGLE_SATELLITE
        constellationName: "Орбита-1"
        satelliteName: "Связь-1"
        cron: "0 */3 * * * *"           # каждые 3 минуты

logging:
  level:
    ru.bmstu.scheduler: INFO
```

### Поля конфигурации миссии

| Поле | Тип | Обязательно | Описание |
|------|-----|-------------|----------|
| `targetType` | String | ✅ | `CONSTELLATION` или `SINGLE_SATELLITE` |
| `constellationName` | String | ✅ | Название группировки |
| `satelliteName` | String | ❌ | Имя спутника (обязательно для `SINGLE_SATELLITE`) |
| `cron` | String | ✅ | Cron-выражение (6 полей) |

---

## API Эндпоинты

### 1. Просмотр миссий

#### `GET /api/scheduler/missions` — Список всех миссий

**Response (200 OK):**
```json
[
  {
    "targetType": "CONSTELLATION",
    "constellationName": "Орбита-1",
    "satelliteName": null,
    "cron": "0 */1 * * * *"
  },
  {
    "targetType": "CONSTELLATION",
    "constellationName": "Орбита-2",
    "satelliteName": null,
    "cron": "0 */2 * * * *"
  },
  {
    "targetType": "SINGLE_SATELLITE",
    "constellationName": "Орбита-1",
    "satelliteName": "Связь-1",
    "cron": "0 */3 * * * *"
  }
]
```

**cURL:**
```bash
curl http://localhost:8081/api/scheduler/missions
```

---

#### `GET /api/scheduler/missions/{index}` — Миссия по индексу

**Path Parameters:**
- `index` — индекс миссии в списке (начиная с 0)

**Response (200 OK):**
```json
{
  "targetType": "CONSTELLATION",
  "constellationName": "Орбита-1",
  "satelliteName": null,
  "cron": "0 */1 * * * *"
}
```

**Response (404 Not Found):** при неверном индексе

**cURL:**
```bash
curl http://localhost:8081/api/scheduler/missions/0
```

---

### 2. Ручной запуск миссий

#### `POST /api/scheduler/missions/constellation/{constellationName}/run` — Запуск для группировки

**Path Parameters:**
- `constellationName` — название группировки

**Query Parameters:**
- `activateBeforeMission` (optional, default: `true`) — активировать ли спутники

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Миссия для группировки Орбита-1 успешно выполнена"
}
```

**Response (500 Internal Server Error):**
```json
{
  "status": "error",
  "message": "Ошибка при выполнении миссии для группировки Орбита-1"
}
```

**cURL:**
```bash
# С активацией спутников (по умолчанию)
curl -X POST "http://localhost:8081/api/scheduler/missions/constellation/Орбита-1/run"

# Без активации спутников
curl -X POST "http://localhost:8081/api/scheduler/missions/constellation/Орбита-1/run?activateBeforeMission=false"
```

---

#### `POST /api/scheduler/missions/satellite/{constellationName}/{satelliteName}/run` — Запуск для спутника

**Path Parameters:**
- `constellationName` — название группировки
- `satelliteName` — имя спутника

**Query Parameters:**
- `activateBeforeMission` (optional, default: `true`)

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Миссия для спутника Связь-1 успешно выполнена"
}
```

**Response (500 Internal Server Error):**
```json
{
  "status": "error",
  "message": "Ошибка при выполнении миссии для спутника Связь-1"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8081/api/scheduler/missions/satellite/Орбита-1/Связь-1/run"
```

---

### 3. Статистика и мониторинг

#### `GET /api/scheduler/stats` — Статистика планировщика

**Response (200 OK):**
```json
{
  "totalMissionsConfigured": 3,
  "schedulerRunning": true,
  "missionsByType": {
    "CONSTELLATION": 2,
    "SINGLE_SATELLITE": 1
  }
}
```

| Поле | Описание |
|------|----------|
| `totalMissionsConfigured` | Количество миссий в конфигурации |
| `schedulerRunning` | Статус планировщика (всегда true) |
| `missionsByType` | Распределение миссий по типам |

**cURL:**
```bash
curl http://localhost:8081/api/scheduler/stats
```

---

#### `GET /api/scheduler/health` — Health check

Проверяет доступность основного сервиса (Space Operation Center).

**Response (200 OK) — сервис доступен:**
```json
{
  "status": "UP",
  "service": "mission-scheduler",
  "mainServiceUrl": "http://localhost:8080/api"
}
```

**Response (503 Service Unavailable) — сервис недоступен:**
```json
{
  "status": "DOWN",
  "service": "mission-scheduler",
  "mainServiceUrl": "http://localhost:8080/api"
}
```

**cURL:**
```bash
curl http://localhost:8081/api/scheduler/health
```

---

## Модели данных

### SpaceCenterProperties (Record)
```java
@ConfigurationProperties(prefix = "app.space-center-service")
public record SpaceCenterProperties(
    String url,                          // http://localhost:8080/api
    List<MissionConfig> missions         // Список миссий
) {
    public record MissionConfig(
        String targetType,               // CONSTELLATION или SINGLE_SATELLITE
        String constellationName,
        String satelliteName,            // null для CONSTELLATION
        String cron
    ) {}
}
```

### MissionRequest (в Server)
```java
public record MissionRequest(
    List<String> constellationNames,
    boolean activateBeforeMission,
    boolean showStatusAfterMission      // всегда true в планировщике
) {}
```

### SingleSatelliteMissionRequest
```java
public record SingleSatelliteMissionRequest(
    String constellationName,
    String satelliteName,
    boolean activateBeforeMission
) {}
```

---

## Внутренняя архитектура

### Планировщик задач

```java
@Configuration
@EnableScheduling
public class SchedulerConfig {
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);                    // 5 параллельных миссий
        scheduler.setThreadNamePrefix("mission-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }
}
```

### Регистрация миссий

```java
@Service
public class MissionSchedulerService {
    private final Map<String, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        for (MissionConfig mission : properties.missions()) {
            registerMission(mission);
        }
    }
    
    private void registerConstellationMission(MissionConfig mission) {
        Runnable task = () -> {
            log.info("Запуск миссии для группировки: {}", mission.constellationName());
            client.executeConstellationMission(mission.constellationName(), true);
        };
        
        ScheduledFuture<?> future = taskScheduler.schedule(
            task, 
            new CronTrigger(mission.cron())
        );
        activeTasks.put(key, future);
    }
}
```

### HTTP клиент

```java
@Component
public class SpaceOperationClient {
    private final RestClient restClient;
    
    public boolean executeConstellationMission(String constellationName, boolean activate) {
        MissionRequest request = new MissionRequest(
            List.of(constellationName),
            activate,
            true
        );
        
        restClient.post()
            .uri("/missions")
            .body(request)
            .retrieve()
            .toBodilessEntity();
        
        return true;
    }
}
```

---

## Логирование

### Формат логов

```
2026-04-10T23:19:32.960+03:00  INFO 19160 --- [mission-scheduler] [           main] r.b.s.service.MissionSchedulerService    : 📅 Запланирована миссия для группировки 'Орбита-1' по расписанию: 0 */1 * * * *

2026-04-10T23:20:00.123+03:00  INFO 19160 --- [mission-scheduler] [mission-scheduler-1] r.b.s.service.MissionSchedulerService    : 🕐 [23:20:00] Запуск запланированной миссии для группировки: Орбита-1

2026-04-10T23:20:00.456+03:00  INFO 19160 --- [mission-scheduler] [mission-scheduler-1] r.b.s.client.SpaceOperationClient        : 📡 Отправка запроса на выполнение миссии для группировки: Орбита-1

2026-04-10T23:20:00.789+03:00  INFO 19160 --- [mission-scheduler] [mission-scheduler-1] r.b.s.client.SpaceOperationClient        : ✅ Миссия для группировки Орбита-1 успешно выполнена
```

### При ошибке

```
2026-04-10T23:20:00.456+03:00 ERROR 19160 --- [mission-scheduler] [mission-scheduler-1] r.b.s.client.SpaceOperationClient        : ❌ Ошибка при выполнении миссии для группировки Орбита-1: Connection refused: localhost/127.0.0.1:8080
```

---

## Сценарии работы

### Сценарий 1: Автоматическое выполнение по расписанию

```
1. Приложение стартует
   ↓
2. Читает application.yaml (3 миссии)
   ↓
3. Регистрирует задачи в TaskScheduler
   ↓
4. Через 1 минуту → миссия для "Орбита-1"
5. Через 2 минуты → миссия для "Орбита-2"
6. Через 3 минуты → миссия для "Связь-1"
   ↓
7. (повторяется согласно cron)
```

### Сценарий 2: Ручной запуск через API

```bash
# Пользователь отправляет запрос
curl -X POST "http://localhost:8081/api/scheduler/missions/constellation/Орбита-1/run"

# Планировщик немедленно выполняет миссию
# Логирует результат
# Возвращает ответ клиенту
```

### Сценарий 3: Основной сервис недоступен

```
1. Планировщик пытается выполнить миссию
   ↓
2. RestClient получает ConnectionRefused
   ↓
3. Ошибка логируется (ERROR)
   ↓
4. Метод возвращает false
   ↓
5. Планировщик продолжает работу (не падает)
   ↓
6. Следующая миссия по расписанию выполнится
```

---

## Тестирование

### Пример теста для контроллера

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SpaceOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MissionSchedulerService schedulerService;

    @Test
    void testGetMissions() throws Exception {
        mockMvc.perform(get("/api/scheduler/missions"))
                .andExpect(status().isOk());
    }

    @Test
    void testRunConstellationMission() throws Exception {
        when(schedulerService.runConstellationMissionNow(eq("Орбита-1"), eq(true)))
            .thenReturn(true);

        mockMvc.perform(post("/api/scheduler/missions/constellation/Орбита-1/run"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}
```
---

## Зависимости (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    
    // implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
}
```