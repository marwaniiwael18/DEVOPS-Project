pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.73.128:9000'
        SONARQUBE_TOKEN = credentials('sonar-api-token')
    }

    stages {
        stage('Git Checkout') {
            steps {
                echo "Starting Git Checkout..."
                git branch: 'registration', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
                echo "Git Checkout completed!"
            }
        }

        stage('Maven Build') {
            steps {
                echo "Starting Maven Build..."
                sh 'mvn clean install'
                echo "Maven Build completed!"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo "Starting SonarQube Analysis..."
                sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=tn.esprit.myspringapp \
                    -Dsonar.host.url=${SONARQUBE_SERVER} \
                    -Dsonar.login=${SONARQUBE_TOKEN}
                """
                echo "SonarQube Analysis completed!"
            }
        }
    }

    post {
        success {
            echo "Build and SonarQube analysis successful!"
        }
        failure {
            echo "Build or SonarQube analysis failed!"
        }
    }
}