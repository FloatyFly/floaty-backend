# ------ Build Stage
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY api api
COPY src src
RUN mvn clean package -DskipTests

# ----- Package stage
FROM eclipse-temurin:17-jre-jammy

ARG spring_profile=local-h2
ENV SPRING_PROFILES_ACTIVE=$spring_profile

COPY --from=build /app/target/floaty-0.0.1-SNAPSHOT.jar /floaty.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/floaty.jar"]
