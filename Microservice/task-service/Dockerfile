FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY task-service/build/libs/task-service-*.jar taskapp.jar
COPY common/build/libs/common-*.jar common.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "taskapp.jar"]