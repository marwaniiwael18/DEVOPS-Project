 
# Utiliser une image OpenJDK 17
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier JAR généré par Maven depuis le dossier target
COPY target/*.jar app.jar

# Exposer le port sur lequel l'application tourne
EXPOSE 8089

# Lancer l'application au démarrage du conteneur
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
