FROM maven:3.8.6-openjdk-17 AS builder

WORKDIR /app
COPY . /app

RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
