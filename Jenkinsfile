pipeline {
    agent any

    environment {
        gitBranch = "Yassine/Skier"
        gitRepo = "https://github.com/marwaniiwael18/DEVOPS-Project.git"

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














    post {
        success {
            echo "✅ Pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed! Check the logs."
        }
    }
}