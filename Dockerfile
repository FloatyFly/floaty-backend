# ------ Build Stage
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline -B

COPY api api
COPY src src

# Add a build argument to control test execution
ARG SKIP_TESTS=true
RUN if [ "$SKIP_TESTS" = "true" ]; then \
      mvn clean package -DskipTests; \
    else \
      mvn clean package; \
    fi

# ----- Package stage
FROM eclipse-temurin:17-jre-jammy

COPY --from=build /app/target/floaty-0.0.1-SNAPSHOT.jar /floaty.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/floaty.jar"]
