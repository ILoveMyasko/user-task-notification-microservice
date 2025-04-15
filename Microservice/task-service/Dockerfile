FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY task-org.main.service/build/libs/task-org.main.service-*.jar taskapp.jar
COPY common/build/libs/common-*.jar common.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "taskapp.jar"]