FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app
###Can build through gradle but it may take a lot of time to build a container (and more space)
COPY user-service/build/libs/user-service-*.jar userapp.jar
### can stay the same for every microservice? (8081:8080, 8082:8080 etc)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "userapp.jar"]