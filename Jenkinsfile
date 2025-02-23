pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'http://192.168.73.128:9000'  // SonarQube URL
        SONARQUBE_TOKEN = credentials('sonar-api-token')  // SonarQube token
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'registration', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    sh "mvn sonar:sonar -Dsonar.projectKey=your_project_key -Dsonar.host.url=${SONARQUBE_SERVER} -Dsonar.login=${SONARQUBE_TOKEN}"
                }
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
