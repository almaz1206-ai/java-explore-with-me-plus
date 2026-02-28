FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY ewm-stats-service ./ewm-stats-service
COPY ewm-main-service/pom.xml ./ewm-main-service/pom.xml
COPY ewm-main-service/src ./ewm-main-service/src
RUN mvn clean package -DskipTests -pl ewm-main-service -am

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/ewm-main-service/target/ewm-main-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
