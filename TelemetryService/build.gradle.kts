plugins {
    java
    application
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
}

group = "ru.bmstu.telemetry"
version = "1.0-SNAPSHOT"

application {
    mainClass = "ru.bmstu.telemetry.TelemetryApplication"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // gRPC dependencies
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE") {
        exclude(group = "io.grpc", module = "grpc-netty-shaded")
    }
    implementation("io.grpc:grpc-protobuf:1.68.1")
    implementation("io.grpc:grpc-stub:1.68.1")
    implementation("io.grpc:grpc-netty:1.68.1")
    implementation("io.grpc:grpc-core:1.68.1")  // Явно добавляем core

    // Для устранения Census ошибок (опционально)
    runtimeOnly("io.grpc:grpc-services:1.68.1")  // содержит health service и reflection

    compileOnly("org.apache.tomcat:annotations-api:6.0.53")

    implementation("org.springframework.kafka:spring-kafka:3.2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("org.flywaydb:flyway-core:10.11.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.11.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Для healthcheck
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                "build/generated/source/proto/main/java",
                "build/generated/source/proto/main/grpc"
            )
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "ru.bmstu.telemetry.TelemetryApplication")
    }
}