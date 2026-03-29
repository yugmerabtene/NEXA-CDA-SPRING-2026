# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/target/authapp-code-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
