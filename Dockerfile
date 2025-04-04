FROM maven:3.8.4-openjdk-11 AS builder

WORKDIR /app

# Copy entire project at once (for reliable dependency resolution)
COPY . .

# Download dependencies (offline)
RUN mvn dependency:go-offline

# Package the application
RUN mvn package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
