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
                        // Step 1: Build and test with MySQL connection parameters (generates jacoco.xml)
                        sh """
                            mvn clean install \
                            -Dspring.datasource.url=jdbc:mysql://192.168.77.129:3306/stationSki?createDatabaseIfNotExist=true \
                            -Dspring.datasource.username=root \
                            -Dspring.datasource.password= \
                            -Dspring.jpa.hibernate.ddl-auto=update
                        """

                        // Step 2: Show content of JaCoCo report folder
                        sh 'ls -l target/site/jacoco/'

                        // Step 3: Run SonarQube analysis with coverage report
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=tn.esprit.myspringapp \
                            -Dsonar.host.url=${SONARQUBE_SERVER} \
                            -Dsonar.login=${SONARQUBE_TOKEN} \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
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
        stage('Run Application') {
            steps {
                script {
                    echo "Starting the application container..."
                    // Pull the latest image we just pushed to Nexus
                    sh "docker pull ${registry}/myspringapp:${BUILD_NUMBER}"

                    // Stop and remove any existing container with the same name to avoid conflicts
                    sh "docker stop myspringapp-container || true"
                    sh "docker rm myspringapp-container || true"

                    // Run the Docker container with appropriate configurations
                    sh """
                        docker run -d \
                        --name myspringapp-container \
                        -p 8080:8080 \
                        -e SPRING_DATASOURCE_URL=jdbc:mysql://192.168.77.129:3306/stationSki?createDatabaseIfNotExist=true \
                        -e SPRING_DATASOURCE_USERNAME=root \
                        -e SPRING_DATASOURCE_PASSWORD= \
                        -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                        ${registry}/myspringapp:${BUILD_NUMBER}
                    """

                    // Verify the container is running
                    sh "docker ps | grep myspringapp-container"

                    // Wait for application to start up properly
                    sh "sleep 10"

                    // Optionally check if the application is responding
                    sh "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/health || echo 'Health check failed'"
                }
            }
            post {
                success {
                    echo "Application is now running successfully!"
                }
                failure {
                    echo "Failed to start the application!"
                    // Optionally get logs to debug
                    sh "docker logs myspringapp-container"
                }
            }
        }
        stage('Start Monitoring Stack') {
            steps {
                script {
                    echo "Checking Docker and Docker Compose installation..."
                    sh 'which docker || echo "Docker not found"'
                    sh 'docker --version || echo "Docker version command failed"'
                    sh 'which docker-compose || echo "docker-compose not found"'
                    sh 'docker-compose --version || echo "docker-compose version command failed"'
            
                    echo "Checking docker-compose.yml file..."
                    sh 'ls -la'
                    sh 'cat docker-compose.yml || echo "docker-compose.yml file not found"'
            
                    echo "Attempting to start monitoring stack..."
                    sh 'docker-compose -f docker-compose.yml up -d prometheus grafana || echo "Command failed with code $?"'
                }
            }
        }
    }
}
