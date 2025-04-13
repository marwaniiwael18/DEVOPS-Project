pipeline {
    agent any

    environment {
        gitBranch = "Yassine/Skier"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"
        registryCredentials = "nexus"
        registry = "10.0.2.15:8083"
        imageName = "yassinemanai_4twin3_thunder_gestionski"
        imageTag = "6.0-SNAPSHOT-${env.BUILD_NUMBER}"

        // SonarQube
        SONAR_URL = "http://10.0.2.15:9000/"
        SONAR_TOKEN = "squ_bda70cd162139dc37d61bec3996c7f06b74aa296"
        SONAR_PROJECT_KEY = "YassineManai_4twin3_gestionski_v2"
        SONAR_PROJECT_NAME = "YassineManai-4Twin3-GestionSki-V2"
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    git branch: gitBranch, url: gitRepo
                    sh 'ls -l'  // Verify Files
                }
            }
        }

        stage('Install Dependencies') {
            steps {
                script {
                    sh 'mvn clean install -DskipTests'  // Clean & Install Dependencies
                }
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
          stage('JaCoCo Report') {
                    steps {
                        jacoco(execPattern: 'target/jacoco.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java', exclusionPattern: '**/test/**')
                    }
                }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'SonarScan'
                    withSonarQubeEnv {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sourceEncoding=UTF-8 \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.login=${SONAR_TOKEN} \
                            -Dsonar.scanner.force-deprecated-java-version=true
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
                    sh 'ls -l'  // Verify Dockerfile presence
                    // Build with the full registry path in the image name
                  sh "docker build --network=host -t ${registry}/${imageName}:${imageTag} ."

                }
            }
        }

          stage('Deploy to Nexus') {  // Keep this inside 'stages'
                   steps {
                       script {
                           docker.withRegistry("http://${registry}", registryCredentials) {
                               sh "docker push ${registry}/${imageName}:${imageTag}"
                           }
                       }
                   }
               }
               stage('Archive Artifacts') {  // Ensure this is inside 'stages'
                   steps {
                       archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                   }
               }

                     stage('Run Application') {
                              steps {
                                  script {
                                      // Libère le port 8081 s’il est déjà utilisé
                                      sh 'fuser -k 8081/tcp || true'

                                      // Arrêt des conteneurs existants et suppression des orphelins
                                      sh 'docker-compose down --remove-orphans || true'

                                      writeFile file: 'docker-compose.yml', text: """
                  version: '3.8'
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
    }

    post {
        success {
            echo "✅ Pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed! Check the logs."
            archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}