pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.77.129:9000'
        SONARQUBE_TOKEN = credentials('scanner')  // Ensure 'scanner' matches the Jenkins credential ID
        registryCredentials = "nexus"  // Nexus registry credentials ID
        registry = "192.168.77.129:8083"  // Nexus Docker registry URL
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'registration', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    try {
                        echo "Running JUnit tests using external MySQL..."
                        sh """
                            mvn test \
                            -Dspring.datasource.url=jdbc:mysql://192.168.77.129:3306/stationSki?createDatabaseIfNotExist=true \
                            -Dspring.datasource.username=root \
                            -Dspring.datasource.password= \
                            -Dspring.jpa.hibernate.ddl-auto=update
                        """
                    } catch (Exception e) {
                        echo "JUnit tests failed: ${e}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    try {
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=tn.esprit.myspringapp \
                            -Dsonar.host.url=${SONARQUBE_SERVER} \
                            -Dsonar.login=${SONARQUBE_TOKEN}
                        """
                    } catch (Exception e) {
                        echo "SonarQube analysis failed: ${e}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('Package Application') {
            steps {
                script {
                    echo "Packaging the application..."
                    sh "mvn package -DskipTests"
                    
                    // Archive the packaged .jar file in Jenkins
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
            post {
                success {
                    echo "Application packaged successfully!"
                }
                failure {
                    echo "Failed to package application!"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image..."
                    // Build and tag the Docker image with the build number
                    sh "docker build -t ${registry}/myspringapp:${BUILD_NUMBER} ."
                    // Optionally, tag the image as 'latest'
                    sh "docker tag ${registry}/myspringapp:${BUILD_NUMBER} ${registry}/myspringapp:latest"
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    // Upload Docker Image to Nexus Registry
                    docker.withRegistry("http://${registry}", registryCredentials) {
                        // Push the Docker image with both tags: BUILD_NUMBER and latest
                        sh "docker push ${registry}/myspringapp:${BUILD_NUMBER}"
                        sh "docker push ${registry}/myspringapp:latest"
                    }
                }
            }
        }

        stage('Start Monitoring Stack') {
            steps {
                script {
                    echo "Starting Prometheus and Grafana via Docker Compose..."
                    sh 'docker compose -f docker-compose.yml up -d prometheus grafana'
                }
            }
        }
    }
}
