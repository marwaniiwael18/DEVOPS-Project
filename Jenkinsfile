pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.73.128:9000'
        SONARQUBE_TOKEN = credentials('sonar-api-token')
        DOCKER_IMAGE_NAME = 'my-spring-app'
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

        stage('SonarQube Analysis') {
            steps {
                script {
                    try {
                        echo "Running SonarQube analysis..."
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=tn.esprit.myspringapp \
                            -Dsonar.host.url=${SONARQUBE_SERVER} \
                            -Dsonar.login=${SONARQUBE_TOKEN}
                        """
                    } catch (Exception e) {
                        echo "SonarQube analysis failed: ${e}"
                        throw e
                    }
                }
            }
        }

        stage('Build Docker Image') {
            agent {
                docker {
                    image 'docker:latest'
                    args '-v /var/run/docker.sock:/var/run/docker.sock'
                }
            }
            steps {
                script {
                    echo "Building Docker image..."
                    sh """
                        docker build -t ${DOCKER_IMAGE_NAME}:latest .
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline executed successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}