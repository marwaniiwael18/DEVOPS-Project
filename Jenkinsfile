pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'subscription-wael', credentialsId: 'github', url: 'https://github.com/marwaniiwael18/DEVOPS-Project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -Dmaven.repo.local=/var/jenkins_home/.m2/repository'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test -Dmaven.repo.local=/var/jenkins_home/.m2/repository'
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