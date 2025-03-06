# Étape 1: Construction de l'application avec Maven
FROM maven:3.8.8-eclipse-temurin-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier seulement les fichiers nécessaires pour optimiser le cache Docker
COPY pom.xml ./
RUN mvn dependency:go-offline --no-transfer-progress

# Copier le reste des fichiers du projet
COPY src/ ./src/

# Construire le projet avec Maven, en évitant les tests
RUN mvn clean package -DskipTests --no-transfer-progress && rm -rf /root/.m2/repository

# Étape 2: Créer l'image finale avec OpenJDK pour exécuter le JAR
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré depuis l'étape précédente
COPY --from=builder /app/target/*.jar /app/app.jar

# Exposer le port 8089 pour l'application
EXPOSE 8089

# Lancer l'application avec la commande java -jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]