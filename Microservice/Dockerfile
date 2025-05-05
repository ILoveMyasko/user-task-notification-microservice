FROM gradle:8.4-jdk21 AS builder
WORKDIR /workspace

COPY settings.gradle settings.gradle
COPY build.gradle build.gradle

RUN gradle --no-daemon dependencies

COPY common common
COPY task-service task-service
COPY user-service user-service
COPY notification-service notification-service

RUN gradle --no-daemon clean bootJar -x test


FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app
ARG SERVICE_DIR
ARG SERVER_PORT


COPY --from=builder \
     /workspace/${SERVICE_DIR}/build/libs/${SERVICE_DIR}-0.0.1-SNAPSHOT.jar \
     ./app.jar

EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]