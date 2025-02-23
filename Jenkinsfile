pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'  // Nom configuré dans Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'subscription-wael', credentialsId: 'github', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'scanner'
                    withSonarQubeEnv(SONARQUBE_SERVER) {
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=gestion-station-ski"
                    }
                }
            }
        }
    }
}