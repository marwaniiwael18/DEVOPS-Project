pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "http://172.20.0.4:8081"  // Nexus Repository
        imageName = "gestion-station-ski"
        imageTag = "1.0-${env.BUILD_NUMBER}"  // Unique Tag per Build
        gitBranch = "subscription-wael"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"
        SONAR_URL = "http://172.20.0.3:9000"  // SonarQube URL
        SONAR_TOKEN = "squ_65d3b090f57666eaa1f74c863a93e4010b788917"
        SONAR_PROJECT_KEY = "gestion-station-ski"
        SONAR_PROJECT_NAME = "Gestion Station Ski"
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

        stage('Run Unit Tests') {
            steps {
                script {
                    sh 'mvn test -Dspring.profiles.active=test'  // Run Unit Tests
                }
            }
        }

        stage('Create SonarQube Project') {
            steps {
                script {
                    sh """
                        curl -u ${SONAR_TOKEN}: -X POST "${SONAR_URL}/api/projects/create" \
                          -d "project=${SONAR_PROJECT_KEY}&name=${SONAR_PROJECT_NAME}"
                    """
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'SonarScan'
                    withSonarQubeEnv('SonarQube') {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sourceEncoding=UTF-8 \
                            -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'ls -l'  // Ensure Dockerfile Exists
                    sh "docker build -t $registry/$imageName:$imageTag ."
                }
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker push $registry/$imageName:$imageTag"
                    }
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh "docker pull $registry/$imageName:$imageTag"
                        sh "sed -i 's|IMAGE_TAG|${imageTag}|g' docker-compose.yml"
                        sh "cat docker-compose.yml"
                        sh "docker-compose up -d"
                    }
                }
            }
        }

        stage("Run Monitoring (Prometheus & Grafana)") {
            steps {
                script {
                    sh 'docker start prometheus || docker run -d --name prometheus prom/prometheus'
                    sh 'docker start grafana || docker run -d --name grafana grafana/grafana'
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
        }
    }
}