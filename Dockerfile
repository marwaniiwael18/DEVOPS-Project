# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/gestion-station-ski-*.jar app.jar

# Expose the port your application runs on
EXPOSE 8089

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]