FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY ewm-main-service/pom.xml ./ewm-main-service/pom.xml
COPY ewm-stats-service ./ewm-stats-service
RUN mvn clean package -DskipTests -pl ewm-stats-service/stats-server -am

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/ewm-stats-service/stats-server/target/stats-server.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
