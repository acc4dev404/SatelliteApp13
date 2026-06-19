# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle clean build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
RUN addgroup -g 1000 -S appgroup && adduser -u 1000 -S appuser -G appgroup
COPY --from=build /app/build/libs/TelemetryService-1.0-SNAPSHOT.jar app.jar
USER appuser
EXPOSE 9090 9091
ENTRYPOINT ["java", "-jar", "app.jar"]