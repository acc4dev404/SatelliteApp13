plugins {
    java
    application
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.bmstu.scheduler"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("ru.bmstu.scheduler.MissionSchedulerApplication")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Для healthcheck
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // OpenAPI / Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka:3.2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")

    // Configuration Processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "ru.bmstu.scheduler.MissionSchedulerApplication")
    }
}