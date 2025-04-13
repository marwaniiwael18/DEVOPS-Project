pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
        dockerHubRepo = 'marwaniwael/gestion-ski'
        imageTag = "1.0-${env.BUILD_NUMBER}"
        dockerHubCredentials = 'docker-hub'
        EMAIL_RECIPIENTS = 'marwani.wael88@gmail.com'
        EMAIL_SENDER = 'marwani.wael88@gmail.com'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'subscription-wael', credentialsId: 'github', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile jacoco:prepare-agent'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test jacoco:report'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                jacoco(
                    execPattern: 'target/jacoco.exec',
                    classPattern: 'target/classes',
                    sourcePattern: 'src/main/java',
                    exclusionPattern: '**/test/**'
                )
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'scanner'
                    withSonarQubeEnv("${SONARQUBE_SERVER}") {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \\
                            -Dsonar.projectKey=gestion-station-ski \\
                            -Dsonar.sources=src/main/java \\
                            -Dsonar.tests=src/test/java \\
                            -Dsonar.java.binaries=target/classes \\
                            -Dsonar.junit.reportsPath=target/surefire-reports \\
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Create Dockerfile
                    writeFile file: 'Dockerfile', text: '''
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
'''
                    sh "docker build -t ${dockerHubRepo}:${imageTag} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', dockerHubCredentials) {
                        sh "docker push ${dockerHubRepo}:${imageTag}"
                    }
                }
            }
        }

        stage('Run Application') {
            steps {
                script {
                    sh 'docker-compose down || true'

                    // Create docker-compose.yml
                    writeFile file: 'docker-compose.yml', text: """
version: '3'
services:
  spring_backend:
    image: ${dockerHubRepo}:${imageTag}
    container_name: spring_backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:h2:mem:testdb
      SPRING_DATASOURCE_DRIVER_CLASS_NAME: org.h2.Driver
      SPRING_DATASOURCE_USERNAME: sa
      SPRING_DATASOURCE_PASSWORD: TestDbStrongP@ss123
      SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.H2Dialect
      SPRING_H2_CONSOLE_ENABLED: "true"
      SPRING_H2_CONSOLE_PATH: /h2-console
      SERVER_PORT: 8081
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_SHOW_SQL: "true"
      LOGGING_LEVEL_ROOT: info
      LOGGING_PATTERN_CONSOLE: "%d{yyyy-MM-dd HH:mm:ss} - %-5level - %logger{45} - %msg %n"
      TZ: Africa/Tunis
    ports:
      - "8081:8081"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: always
"""
                    sh 'docker-compose up -d'
                }
            }
        }

        stage("Run Prometheus") {
            steps {
                script {
                    sh 'docker start prometheus || docker run -d --name prometheus -p 9090:9090 prom/prometheus'
                }
            }
        }

        stage("Run Grafana") {
            steps {
                script {
                    sh 'docker start grafana || docker run -d --name grafana -p 3000:3000 grafana/grafana'
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build successful!"
            emailext(
                subject: "✅ [JENKINS] Build #${BUILD_NUMBER} - ${currentBuild.currentResult} - ${env.JOB_NAME}",
                body: """
                    <html>
                    <body>
                        <h2>✅ Build Successful: ${env.JOB_NAME}</h2>
                        <p>Build #${BUILD_NUMBER} completed successfully!</p>

                        <h3>Build Info:</h3>
                        <ul>
                            <li>Status: ${currentBuild.currentResult}</li>
                            <li>Job: ${env.JOB_NAME}</li>
                            <li>Tag: ${dockerHubRepo}:${imageTag}</li>
                            <li>Build URL: <a href="${BUILD_URL}">${BUILD_URL}</a></li>
                            <li>Duration: ${currentBuild.durationString}</li>
                        </ul>

                        <p><a href="${BUILD_URL}console">View Console Output</a></p>
                        <p>-- Jenkins CI/CD</p>
                    </body>
                    </html>
                """,
                to: "${EMAIL_RECIPIENTS}",
                from: "${EMAIL_SENDER}",
                mimeType: 'text/html',
                attachLog: true
            )
        }

        failure {
            echo "❌ Build failed!"
            emailext(
                subject: "❌ [JENKINS] Build #${BUILD_NUMBER} - ${currentBuild.currentResult} - ${env.JOB_NAME}",
                body: """
                    <html>
                    <body>
                        <h2>❌ Build Failed: ${env.JOB_NAME}</h2>
                        <p>Build #${BUILD_NUMBER} has failed!</p>

                        <h3>Build Info:</h3>
                        <ul>
                            <li>Status: ${currentBuild.currentResult}</li>
                            <li>Job: ${env.JOB_NAME}</li>
                            <li>Tag: ${dockerHubRepo}:${imageTag}</li>
                            <li>Build URL: <a href="${BUILD_URL}">${BUILD_URL}</a></li>
                            <li>Duration: ${currentBuild.durationString}</li>
                        </ul>

                        <p><a href="${BUILD_URL}console">View Console Output</a></p>
                        <p>-- Jenkins CI/CD</p>
                    </body>
                    </html>
                """,
                to: "${EMAIL_RECIPIENTS}",
                from: "${EMAIL_SENDER}",
                mimeType: 'text/html',
                attachLog: true
            )
        }
    }
}