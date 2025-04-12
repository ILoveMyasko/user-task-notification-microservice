FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY notification-service/build/libs/notification-service-*.jar notificationapp.jar
COPY common/build/libs/common-*.jar common.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "notificationapp.jar"]